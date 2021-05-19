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

import com.github.housepower.annotation.Issue
import com.github.housepower.jdbc.AbstractITest
import com.github.housepower.jdbc.tool.TestHarness
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.to_date
import org.apache.spark.sql.jdbc.{ClickHouseDialect, JdbcDialects}
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.junit.jupiter.api.{BeforeAll, Test}

import java.sql.Date
import java.time.LocalDate
import scala.collection.Seq

object SparkDateITest {
  @BeforeAll
  def beforeAll(): Unit = {
    // make sure register `ClickHouseDialects` before we use it
    JdbcDialects.registerDialect(ClickHouseDialect)
  }
}


class SparkDateITest extends AbstractITest with Logging {

  @Test
  @Issue(channel = "GitHub", value = "#343",
    link = "https://github.com/housepower/ClickHouse-Native-JDBC/issues/343")
  def testSparkJdbcWrite(): Unit = {
    val helper = new TestHarness("Date")
    helper.clean()
    helper.create()
    doSparkWriteAndRead(helper.getTableName)
    helper.clean()
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

  // col_0 Date
  @transient lazy implicit val schema: StructType = StructType.apply(
    StructField("col_0", DataTypes.DateType, nullable = false) :: Nil)

  private def doSparkWriteAndRead(table: String): Unit = {
    import spark.implicits._

    val df = Seq("2020-10-27")
      .toDF("col_0")
      .withColumn("col_0", to_date($"col_0"))

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

    val rows = spark.read
      .format("jdbc")
      .option("driver", "com.github.housepower.jdbc.ClickHouseDriver")
      .option("url", getJdbcUrl)
      .option("user", "default")
      .option("password", "")
      .option("dbtable", table)
      .load
      .collect

    assert(rows.length == 1)
    assert(rows(0).getDate(0) == Date.valueOf(LocalDate.of(2020, 10, 27)))
  }
}
