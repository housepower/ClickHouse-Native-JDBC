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

import com.github.housepower.jdbc.AbstractITest
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession
import org.junit.jupiter.api.{Disabled, Test}

class ClickHouseCatalogITest extends AbstractITest with Logging {

  @Test
  def test(): Unit = {
    spark.sql("create database test_1").collect
    spark.sql("create database test_2").collect
    spark.sql("show databases").show(false)
    spark.sql("use system").collect
    spark.sql("show tables").show(false)

    spark.sql(
      """
        | CREATE TABLE default.spark_tbl (
        |   a INT NOT NULL,
        |   b LONG NOT NULL,
        |   c STRING
        | ) USING ClickHouse
        | PARTITIONED BY (toDate(a))
        | TBLPROPERTIES (
        | engine = 'MergeTree()',
        | order_by = '(b)',
        | settings.index_granularity = 8192
        | )
        |""".stripMargin)

    spark.createDataFrame(Seq((1, 1L, "1"), (2, 2L, "2")))
      .toDF("a", "b", "c")
      .writeTo("clickhouse.default.spark_tbl")
      .append

    spark.sql(""" DESC default.spark_tbl """).show(false)
  }

  @transient lazy implicit val spark: SparkSession = {
    SparkSession.builder()
      .master("local[2]")
      .appName("spark-ut")
      .config("spark.ui.enabled", "false")
      .config("spark.driver.host", "localhost")
      .config("spark.sql.shuffle.partitions", "2")
      .config("spark.sql.defaultCatalog", "clickhouse")
      .config("spark.sql.catalog.clickhouse", "com.github.housepower.spark.ClickHouseCatalog")
      .config("spark.sql.catalog.clickhouse.host", AbstractITest.CK_HOST)
      .config("spark.sql.catalog.clickhouse.port", AbstractITest.CK_GRPC_PORT)
      .config("spark.sql.catalog.clickhouse.user", AbstractITest.CLICKHOUSE_USER)
      .config("spark.sql.catalog.clickhouse.password", AbstractITest.CLICKHOUSE_PASSWORD)
      .config("spark.sql.catalog.clickhouse.database", AbstractITest.CLICKHOUSE_DB)
      .getOrCreate()
  }
}
