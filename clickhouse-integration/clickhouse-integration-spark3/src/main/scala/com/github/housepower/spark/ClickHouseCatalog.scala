/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.housepower.spark

import java.time.ZoneId
import java.util

import scala.collection.JavaConverters._

import com.github.housepower.client.GrpcConnection
import com.github.housepower.client.NativeContext.ServerContext
import com.github.housepower.data.DataTypeFactory
import com.github.housepower.settings.ClickHouseConfig
import org.apache.spark.internal.Logging
import org.apache.spark.sql.ClickHouseAnalysisException
import org.apache.spark.sql.catalyst.analysis.{NoSuchDatabaseException, NoSuchNamespaceException, NoSuchTableException}
import org.apache.spark.sql.connector.catalog._
import org.apache.spark.sql.connector.expressions.Transform
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap

class ClickHouseCatalog extends TableCatalog with SupportsNamespaces with Logging {

  private var catalogName: String = _

  private var currentDb: String = _

  private var cfg: ClickHouseConfig = _

  private var grpcConn: GrpcConnection = _

  import JacksonUtil._

  override def initialize(name: String, options: CaseInsensitiveStringMap): Unit = {
    this.catalogName = name
    val host = options.getOrDefault("host", "localhost")
    val port = options.getInt("port", 9100)
    val user = options.getOrDefault("user", "default")
    val password = options.getOrDefault("password", "")
    this.currentDb = options.getOrDefault("database", "default")
    this.cfg = ClickHouseConfig.Builder.builder()
      .host(host)
      .port(port)
      .user(user)
      .password(password)
      .database(currentDb)
      .build()
    this.grpcConn = GrpcConnection.create(cfg)

    val ex = grpcConn.syncQuery("SELECT now()").getException

    if (ex.getCode != 0)
      throw ClickHouseAnalysisException(s"Error[${ex.getCode}] ${ex.getDisplayText}")
  }

  override def name(): String = catalogName

  override def listTables(namespace: Array[String]): Array[Identifier] = namespace match {
    case Array(database) =>
      val result = grpcConn.syncQuery(s"SHOW TABLES IN ${quote(database)}")
      Option(result.getException)
        .filterNot { ex => ex.getCode == 0 }
        .foreach {
          case ex if ex.getCode == 81 => throw new NoSuchDatabaseException(namespace.mkString("."))
          case ex =>
            throw ClickHouseAnalysisException(s"Error[${ex.getCode}] ${ex.getDisplayText}")
        }

      val output = om.readValue[JSONOutput](result.getOutput)
      output.data.map { row => row.get("name").asText() }
        .map { table => Identifier.of(namespace, table) }
        .toArray

    case _ => throw new NoSuchDatabaseException(namespace.mkString("."))
  }

  override def loadTable(ident: Identifier): ClickHouseTable = {
    quoteTable(ident) match {
      case Some(tbl) =>
        grpcConn.syncQuery(s"SELECT * FROM $tbl WHERE 1=0").getException match {
          case e if e.getCode == 0 => // do nothing
          case e if e.getCode == 60 => throw new NoSuchTableException(ident.toString)
          case e => throw ClickHouseAnalysisException(s"Error[${e.getCode}] ${e.getDisplayText}")
        }
      case None => throw ClickHouseAnalysisException(s"Invalid table identifier: $ident")
    }

    val tableResult = grpcConn.syncQuery(
      s""" SELECT
         |   `database`, `name`, `uuid`, `engine`, `is_temporary`,
         |   `data_paths`, `metadata_path`,
         |   `metadata_modification_time`,
         |   `dependencies_database`, `dependencies_table`,
         |   `create_table_query`,
         |   `engine_full`,
         |   `partition_key`, `sorting_key`, `primary_key`, `sampling_key`,
         |   `storage_policy`,
         |   `total_rows`, `total_bytes`,
         |   `lifetime_rows`, `lifetime_bytes`
         | FROM `system`.`tables`
         | WHERE `database`='${ident.namespace().mkString}' AND `name`='${ident.name()}'
         | """.stripMargin)

    if (tableResult.getException.getCode != 0)
      throw ClickHouseAnalysisException(s"Error[${tableResult.getException.getCode}] ${tableResult.getException.getDisplayText}")

    val tableOutput = om.readValue[JSONOutput](tableResult.getOutput)
    assert(tableOutput.rows == 1)
    val tableJsonArr = tableOutput.data.head
    val props = Map(
      "uuid" -> tableJsonArr.get("uuid").asText,
      "engine" -> tableJsonArr.get("engine").asText,
      "is_temporary" -> tableJsonArr.get("is_temporary").asText,
      "data_paths" -> tableJsonArr.get("data_paths").asText,
      "metadata_path" -> tableJsonArr.get("metadata_path").asText
    ).asJava

    val columnResult = grpcConn.syncQuery(
      s""" SELECT
         |   `database`,
         |   `table`,
         |   `name`,
         |   `type`,
         |   `position`,
         |   `default_kind`,
         |   `default_expression`,
         |   `data_compressed_bytes`,
         |   `data_uncompressed_bytes`,
         |   `marks_bytes`,
         |   `comment`,
         |   `is_in_partition_key`,
         |   `is_in_sorting_key`,
         |   `is_in_primary_key`,
         |   `is_in_sampling_key`,
         |   `compression_codec`
         | FROM `system`.`columns`
         | WHERE `database`='${ident.namespace().mkString}' AND `table`='${ident.name()}'
         | ORDER BY `position` ASC
         | """.stripMargin)

    if (columnResult.getException.getCode != 0)
      throw ClickHouseAnalysisException(s"Error[${columnResult.getException.getCode}] ${columnResult.getException.getDisplayText}")

    val columnOutput = om.readValue[JSONOutput](columnResult.getOutput)
    val schema = ClickHouseSchemaUtil.fromClickHouseSchema(columnOutput.data.map { row =>
      val mockServerCtx = new ServerContext(0L, 0L, 0L, ClickHouseConfig.Builder.builder().build(), ZoneId.systemDefault(), "")
      val fieldName = row.get("name").asText
      val ckType = DataTypeFactory.get(row.get("type").asText, mockServerCtx)
      (fieldName, ckType)
    })
    new ClickHouseTable(ident, schema, props, cfg)
  }

  /**
   *
   * <h2>MergeTree Engine</h2>
   * {{{
   * CREATE TABLE [IF NOT EXISTS] [db.]table_name [ON CLUSTER cluster]
   * (
   *     name1 [type1] [DEFAULT|MATERIALIZED|ALIAS expr1] [TTL expr1],
   *     name2 [type2] [DEFAULT|MATERIALIZED|ALIAS expr2] [TTL expr2],
   *     ...
   *     INDEX index_name1 expr1 TYPE type1(...) GRANULARITY value1,
   *     INDEX index_name2 expr2 TYPE type2(...) GRANULARITY value2
   * ) ENGINE = MergeTree()
   * ORDER BY expr
   * [PARTITION BY expr]
   * [PRIMARY KEY expr]
   * [SAMPLE BY expr]
   * [TTL expr
   *     [DELETE|TO DISK 'xxx'|TO VOLUME 'xxx' [, ...] ]
   *     [WHERE conditions]
   *     [GROUP BY key_expr [SET v1 = aggr_func(v1) [, v2 = aggr_func(v2) ...]] ]]
   * [SETTINGS name=value, ...]
   * }}}
   * <p>
   *
   * <h2>ReplacingMergeTree Engine</h2>
   * {{{
   * CREATE TABLE [IF NOT EXISTS] [db.]table_name [ON CLUSTER cluster]
   * (
   *     name1 [type1] [DEFAULT|MATERIALIZED|ALIAS expr1],
   *     name2 [type2] [DEFAULT|MATERIALIZED|ALIAS expr2],
   *     ...
   * ) ENGINE = ReplacingMergeTree([ver])
   * [PARTITION BY expr]
   * [ORDER BY expr]
   * [PRIMARY KEY expr]
   * [SAMPLE BY expr]
   * [SETTINGS name=value, ...]
   * }}}
   *
   * `ver` — column with version. Type `UInt*`, `Date` or `DateTime`.
   */
  override def createTable(ident: Identifier,
                           schema: StructType,
                           partitions: Array[Transform],
                           properties: util.Map[String, String]): ClickHouseTable = {
    val tbl = quoteTable(ident) match {
      case Some(t) => t
      case None => throw ClickHouseAnalysisException(s"Invalid table identifier: $ident")
    }
    val props = properties.asScala
    val engineExpr = props.get("engine").map { e => s"ENGINE = $e" }.getOrElse {
      throw ClickHouseAnalysisException("Missing property 'engine'")
    }
    val partitionsExpr = partitions match {
      case transforms if transforms.nonEmpty =>
        transforms.map(_.describe).mkString("PARTITION BY (", ", ", ")")
      case _ => ""
    }
    val clusterExpr = props.get("cluster").map { c => s"ON CLUSTER $c" }.getOrElse("")
    val orderExpr = props.get("order_by").map { o => s"ORDER BY $o" }.getOrElse("")
    val primaryKeyExpr = props.get("primary_key").map { p => s"PRIMARY KEY $p" }.getOrElse("")
    val sampleExpr = props.get("sample_by").map { p => s"SAMPLE BY $p" }.getOrElse("")

    val settingsExpr = props.filterKeys(_.startsWith("settings.")) match {
      case settings if settings.nonEmpty =>
        settings.map { case (k, v) => s"${k.substring("settings.".length)}=$v" }.mkString("SETTINGS ", ", ", "")
      case _ => ""
    }

    val fieldsDefinition = ClickHouseSchemaUtil.toClickHouseSchema(schema).map { case (fieldName, ckType) =>
      s"${quote(fieldName)} ${ckType.name()}"
    }.mkString(",\n ")

    val sql =
      s"""
         | CREATE TABLE $tbl $clusterExpr (
         | $fieldsDefinition
         | ) $engineExpr
         | $partitionsExpr
         | $orderExpr
         | $primaryKeyExpr
         | $sampleExpr
         | $settingsExpr
         |""".stripMargin
        .replaceAll("""\n\s+\n""", "\n") // remove empty lines

    val result = grpcConn.syncQuery(sql)
    if (result.getException.getCode != 0)
      throw ClickHouseAnalysisException(result.getException.getDisplayText)

    loadTable(ident)
  }

  override def alterTable(ident: Identifier, changes: TableChange*): ClickHouseTable = throw new UnsupportedOperationException

  override def dropTable(ident: Identifier): Boolean = quoteTable(ident).exists { tbl =>
    val result = grpcConn.syncQuery(s"DROP TABLE $tbl")
    result.getException.getCode == 0
  }

  override def renameTable(oldIdent: Identifier, newIdent: Identifier): Unit =
    (quoteTable(oldIdent), quoteTable(newIdent)) match {
      case (Some(oldTbl), Some(newTbl)) =>
        val result = grpcConn.syncQuery(s"RENAME TABLE $oldTbl to $newTbl")
        if (result.getException.getCode != 0)
          throw new NoSuchTableException(result.getException.getDisplayText)

      case _ => throw ClickHouseAnalysisException("invalid table identifier")
    }

  override def defaultNamespace(): Array[String] = Array(currentDb)

  override def listNamespaces(): Array[Array[String]] = {
    val result = grpcConn.syncQuery("SHOW DATABASES")
    val output = om.readValue[JSONOutput](result.getOutput)
    output.data.map { row => Array(row.get("name").asText) }.toArray
  }

  override def listNamespaces(namespace: Array[String]): Array[Array[String]] = namespace match {
    case Array() => listNamespaces()
    case _ => throw new NoSuchNamespaceException(namespace.map(quote).mkString("."))
  }

  override def loadNamespaceMetadata(namespace: Array[String]): util.Map[String, String] = namespace match {
    case Array(database) =>
      listNamespaces()
        .map { case Array(db) => db }
        .find { db => database == db }
        .map { _ => Map.empty[String, String].asJava }
        .getOrElse {
          throw new NoSuchDatabaseException(namespace.map(quote).mkString("."))
        }
    case _ => throw new NoSuchDatabaseException(namespace.map(quote).mkString("."))
  }

  override def createNamespace(namespace: Array[String],
                               metadata: util.Map[String, String]): Unit = namespace match {
    case Array(database) => grpcConn.syncQuery(s"CREATE DATABASE ${quote(database)}")
  }

  override def alterNamespace(namespace: Array[String], changes: NamespaceChange*): Unit = ???

  override def dropNamespace(namespace: Array[String]): Boolean = namespace match {
    case Array(database) =>
      val result = grpcConn.syncQuery(s"DROP DATABASE ${quote(database)}")
      result.getException == null
    case _ => false
  }

  def quote(token: String) = s"`$token`"

  def quoteTable(ident: Identifier): Option[String] = ident.namespace() match {
    case Array(database) => Some(s"${quote(database)}.${quote(ident.name())}")
    case _ => None
  }
}
