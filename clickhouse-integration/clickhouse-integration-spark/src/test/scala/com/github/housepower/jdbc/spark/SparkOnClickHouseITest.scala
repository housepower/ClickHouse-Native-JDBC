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

package com.github.housepower.jdbc.spark

import com.github.housepower.jdbc.AbstractITest
import com.github.housepower.jdbc.tool.TestHarness
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{to_date, to_timestamp}
import org.apache.spark.sql.jdbc.{ClickHouseDialect, JdbcDialects}
import org.apache.spark.sql.types.{ArrayType, DataTypes, StructField, StructType}
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.{BeforeAll, Test}

import scala.collection.Seq

object SparkOnClickHouseITest {
  @BeforeAll
  def beforeAll(): Unit = {
    // make sure register `ClickHouseDialects` before we use it
    JdbcDialects.registerDialect(ClickHouseDialect)
  }
}

class SparkOnClickHouseITest extends AbstractITest with Logging {

  @Test
  def testSparkJdbcWrite(): Unit = {
    val helper = new TestHarness()
    helper.clean()
    helper.create()
    doSparkJdbcWrite(helper.getTableName)
    helper.clean()
  }

  @Test
  def testSparkJdbcReadAndWrite(): Unit = {
    val sourceHelper = new TestHarness()
    val targetHelper = new TestHarness()
    sourceHelper.clean()
    targetHelper.clean()
    sourceHelper.create()
    targetHelper.create()
    sourceHelper.insert()
    doSparkJdbcReadAndWrite(sourceHelper.getTableName, targetHelper.getTableName)
    // order is not guaranteed
    // targetHelper.checkItem()
    targetHelper.checkAgg()
    targetHelper.clean()
    sourceHelper.clean()
  }

  @Test
  def testAutoCreateTable(): Unit = {
    val sourceHelper = new TestHarness()
    val targetHelper = new TestHarness()
    sourceHelper.clean()
    targetHelper.clean()
    sourceHelper.create()
    // targetHelper.create()
    sourceHelper.insert()
    doSparkJdbcReadAndCreateTableWrite(sourceHelper.getTableName, targetHelper.getTableName)
    // order is not guaranteed
    // targetHelper.checkItem()
    targetHelper.checkAgg()
    targetHelper.clean()
    sourceHelper.clean()
  }

  @transient lazy implicit val spark: SparkSession = {
    SparkSession.builder()
      .master("local[2]")
      .appName("spark-ut")
      .config("spark.ui.enabled", "false")
      .config("spark.driver.host", "localhost")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", System.getProperty("java.io.tmpdir"))
      .getOrCreate()
  }

  // col_0 Int8,
  // col_1 Int16,
  // col_2 Int32,
  // col_3 Int64,
  // col_4 UInt8,
  // col_5 UInt16,
  // col_6 UInt32,
  // col_7 UInt64,
  // col_8 Float32,
  // col_9 Float64,
  // col_10 Nullable(Float64),
  // col_11 String,
  // col_12 Date,
  // col_13 DateTime,
  // col_14 Array(String)
  @transient lazy implicit val schema: StructType = StructType.apply(
    StructField("col_0", DataTypes.ByteType, nullable = false) ::
      StructField("col_1", DataTypes.ShortType, nullable = false) ::
      StructField("col_2", DataTypes.IntegerType, nullable = false) ::
      StructField("col_3", DataTypes.LongType, nullable = false) ::
      StructField("col_4", DataTypes.ShortType, nullable = false) ::
      StructField("col_5", DataTypes.IntegerType, nullable = false) ::
      StructField("col_6", DataTypes.LongType, nullable = false) ::
      StructField("col_7", DataTypes.LongType, nullable = false) ::
      StructField("col_8", DataTypes.FloatType, nullable = false) ::
      StructField("col_9", DataTypes.DoubleType, nullable = false) ::
      StructField("col_10", DataTypes.DoubleType, nullable = true) ::
      StructField("col_11", DataTypes.StringType, nullable = false) ::
      StructField("col_12", DataTypes.DateType, nullable = false) ::
      StructField("col_13", DataTypes.TimestampType, nullable = false) ::
      StructField("col_14", ArrayType(DataTypes.StringType, containsNull = false), nullable = false) :: Nil)

  private def doSparkJdbcWrite(table: String): Unit = {
    import spark.implicits._

    val df = Seq((1.toByte, 1.toShort, 1, 1L, 1.toShort, 1, 1L, 1L, 1.1F, 1.1D, null, "a_1", "2020-10-27", "2020-10-27 01:46:45", Array("哈哈", "哇咔咔", "你好，世界")),
      (2.toByte, 2.toShort, 2, 2L, 2.toShort, 2, 2L, 2L, 2.2F, 2.2D, null, "b_2", "2020-10-27", "2020-10-27 02:46:45", Array("🇨🇳", "🇷🇺", "🇩🇪", "🇯🇵", "🇺🇸")),
      (3.toByte, 3.toShort, 3, 3L, 3.toShort, 3, 3L, 3L, 3.3F, 3.3D, null, "c_3", "2020-10-27", "2020-10-27 03:46:45", Array[String]()))
      .toDF("col_0", "col_1", "col_2", "col_3", "col_4", "col_5", "col_6", "col_7", "col_8", "col_9", "col_10", "col_11", "col_12", "col_13", "col_14")
      .withColumn("col_12", to_date($"col_12"))
      .withColumn("col_13", to_timestamp($"col_13"))

    val resultDf = spark.createDataFrame(df.rdd, schema)

    resultDf
      .write
      .format("jdbc")
      .mode("overwrite")
      .option("driver", "com.github.housepower.jdbc.ClickHouseDriver")
      .option("url", getJdbcUrl)
      .option("user", "default")
      .option("password", "")
      .option("dbtable", table)
      .option("truncate", "true")
      .option("batchsize", 1000)
      .option("isolationLevel", "NONE")
      .save
  }

  private def doSparkJdbcReadAndWrite(sourceTable: String, targetTable: String): Unit = {
    spark.read
      .format("jdbc")
      .option("driver", "com.github.housepower.jdbc.ClickHouseDriver")
      .option("url", getJdbcUrl)
      .option("user", "default")
      .option("password", "")
      .option("dbtable", sourceTable)
      .load
      .write
      .format("jdbc")
      .mode("overwrite")
      .option("driver", "com.github.housepower.jdbc.ClickHouseDriver")
      .option("url", getJdbcUrl)
      .option("user", "default")
      .option("password", "")
      .option("dbtable", targetTable)
      .option("truncate", "true")
      .option("batchsize", 10000)
      .option("isolationLevel", "NONE")
      .save
  }

  private def doSparkJdbcReadAndCreateTableWrite(sourceTable: String, targetTable: String): Unit = {
    val df = spark.read
      .format("jdbc")
      .option("driver", "com.github.housepower.jdbc.ClickHouseDriver")
      .option("url", getJdbcUrl)
      .option("user", "default")
      .option("password", "")
      .option("dbtable", sourceTable)
      .load

    assertEquals(StructType.apply(
      StructField("col_0", DataTypes.ByteType, nullable = true) ::
        StructField("col_1", DataTypes.ShortType, nullable = true) ::
        StructField("col_2", DataTypes.IntegerType, nullable = true) ::
        StructField("col_3", DataTypes.LongType, nullable = true) ::
        StructField("col_4", DataTypes.ShortType, nullable = true) ::
        StructField("col_5", DataTypes.IntegerType, nullable = true) ::
        StructField("col_6", DataTypes.LongType, nullable = true) ::
        StructField("col_7", DataTypes.LongType, nullable = true) ::
        StructField("col_8", DataTypes.FloatType, nullable = true) ::
        StructField("col_9", DataTypes.DoubleType, nullable = true) ::
        StructField("col_10", DataTypes.DoubleType, nullable = true) ::
        StructField("col_11", DataTypes.StringType, nullable = true) ::
        StructField("col_12", DataTypes.DateType, nullable = true) ::
        StructField("col_13", DataTypes.TimestampType, nullable = true) ::
        StructField("col_14", ArrayType(DataTypes.StringType, containsNull = false), nullable = true) :: Nil),
      df.schema)

    df.write
      .format("jdbc")
      .mode("errorifexists")
      .option("createTableOptions", "ENGINE=Log()")
      .option("driver", "com.github.housepower.jdbc.ClickHouseDriver")
      .option("url", getJdbcUrl)
      .option("user", "default")
      .option("password", "")
      .option("dbtable", targetTable)
      .option("truncate", "true")
      .option("batchsize", 10000)
      .option("isolationLevel", "NONE")
      .save

  }
}
