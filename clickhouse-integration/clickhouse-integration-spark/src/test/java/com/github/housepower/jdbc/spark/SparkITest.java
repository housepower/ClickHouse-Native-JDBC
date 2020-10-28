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

package com.github.housepower.jdbc.spark;

import com.github.housepower.jdbc.AbstractITest;
import com.github.housepower.jdbc.ClickHouseArray;
import com.github.housepower.jdbc.tool.TestHarness;
import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.jdbc.ClickHouseDialect$;
import org.apache.spark.sql.jdbc.JdbcDialects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scala.Serializable;
import scala.collection.mutable.WrappedArray;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Properties;

// insert to helper table
// spark insert into test from helper table
// truncate helper table
// insert into helper table from test
// helper table check
public class SparkITest extends AbstractITest implements Serializable {

    @BeforeAll
    public static void beforeAll() {
        // make sure register `ClickHouseDialects$.MODULE$` before we use it
        JdbcDialects.registerDialect(ClickHouseDialect$.MODULE$);
    }

    @Test
    public void successfullySparkByJdbcSink() throws Exception {
        TestHarness helper = new TestHarness();

        helper.clean();
        helper.create();
        helper.insert();

        withNewConnection(connection -> {
            Statement statement = connection.createStatement();
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test as " + helper.getTableName() + " Engine = Memory");
        });

        String connectionStr = getJdbcUrl();
        SparkSession spark = SparkSession
                .builder()
                .appName("spark-jdbc-test")
                .master("local")
                .getOrCreate();
        Dataset<Row> rows = spark.read()
                .jdbc(connectionStr, helper.getTableName(), new Properties());

        Properties properties = new Properties();
        properties.put("user", "default");
        rows.write().mode("append")
                .option("driver", "com.github.housepower.jdbc.ClickHouseDriver")
                .option("user", "default")
                .option("batchsize", "65536")
                .option("isolationLevel", "NONE")
                .jdbc(connectionStr, "default.test", properties);

        withNewConnection(connection -> {
            Statement statement = connection.createStatement();
            statement.executeQuery("TRUNCATE TABLE " + helper.getTableName());
            statement.executeQuery("INSERT INTO " + helper.getTableName() + " SELECT * FROM test");
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
        helper.checkItem();
        helper.checkAggr();
        helper.clean();
    }


    @Test
    public void successfullySparksByRDDSink() throws Exception {
        TestHarness helper = new TestHarness();

        helper.clean();
        helper.create();
        helper.insert();

        withNewConnection(connection -> {
            Statement statement = connection.createStatement();
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test as " + helper.getTableName() + " Engine = Memory");
        });

        String connectionStr = getJdbcUrl();
        SparkSession spark = SparkSession
                .builder()
                .appName("spark-jdbc-test")
                .master("local")
                .getOrCreate();
        Properties properties = new Properties();
        properties.put("user", "default");

        Dataset<Row> rows = spark.read()
                .jdbc(connectionStr, helper.getTableName(), properties);

        String insertSQL = helper.insertSQL()
                .replaceAll(helper.getTableName(), "test");
        int colSize = helper.getTypes().size();

        rows.foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
            withNewConnection(connection -> {
                PreparedStatement stmt = connection.prepareStatement(insertSQL);
                int count = 0;
                while (iterator.hasNext()) {
                    Row row = iterator.next();
                    for (int i = 0; i < colSize; i++) {
                        Object obj = row.get(i);
                        if (obj instanceof WrappedArray) {
                            Object[] arr = (Object[]) ((WrappedArray<?>) obj).array();
                            stmt.setArray(i + 1, new ClickHouseArray(arr));
                        } else {
                            stmt.setObject(i + 1, row.get(i));
                        }
                    }
                    stmt.addBatch();
                    count++;
                }
                if (count > 0) {
                    stmt.executeBatch();
                }
            });
        });

        withNewConnection(connection -> {
            Statement statement = connection.createStatement();
            statement.executeQuery("TRUNCATE TABLE " + helper.getTableName());
            statement.executeQuery("INSERT INTO " + helper.getTableName() + " SELECT * FROM test");
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });

        // we do not check item, because orders are not guaranteed
        helper.checkAggr();
        helper.clean();
    }
}
