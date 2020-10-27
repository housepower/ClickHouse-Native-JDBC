package com.github.housepower.jdbc;

import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Properties;

import scala.Serializable;
import scala.collection.Iterator;
import scala.collection.mutable.WrappedArray;

// insert to genericSimpleInsertITest
// spark insert into test from genericSimpleInsertITest
// truncate genericSimpleInsertITest
// insert into genericSimpleInsertITest from test
// genericSimpleInsertITest check
public class SparkITest extends AbstractITest implements Serializable {


    @Test
    public void successfullySparkByJdbcSink() throws Exception {
        GenericSimpleInsertITest genericSimpleInsertITest = new GenericSimpleInsertITest();
        // TODO currently we did not support Array(String) from jdbc read
        genericSimpleInsertITest.removeType("Array(String)");

        genericSimpleInsertITest.clean();
        genericSimpleInsertITest.create();
        genericSimpleInsertITest.insert();

        withNewConnection(connection -> {
            Statement statement = connection.createStatement();
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery(
                "CREATE TABLE test as " + genericSimpleInsertITest.tableName + " Engine = Memory");
        });

        String connectionStr = getJdbcUrl();
        SparkSession spark = SparkSession
                                 .builder()
                                 .appName("spark-jdbc-test")
                                 .master("local")
                                 .getOrCreate();
        Dataset<Row>
            rows =
            spark.read()
                .jdbc(connectionStr, genericSimpleInsertITest.getTableName(), new Properties());

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
            statement.executeQuery("TRUNCATE TABLE " + genericSimpleInsertITest.tableName);
            statement.executeQuery(
                "INSERT INTO " + genericSimpleInsertITest.tableName + " SELECT * FROM test");
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
        genericSimpleInsertITest.checkItem();
        genericSimpleInsertITest.checkAggr();
        genericSimpleInsertITest.clean();
    }


    @Test
    public void successfullySparksByRDDSink() throws Exception {
        GenericSimpleInsertITest genericSimpleInsertITest = new GenericSimpleInsertITest();
        // TODO currently we did not support Array(String) from jdbc read
        genericSimpleInsertITest.removeType("Array(String)");

        genericSimpleInsertITest.clean();
        genericSimpleInsertITest.create();
        genericSimpleInsertITest.insert();

        withNewConnection(connection -> {
            Statement statement = connection.createStatement();
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery(
                "CREATE TABLE test as " + genericSimpleInsertITest.tableName + " Engine = Memory");
        });

        String connectionStr = getJdbcUrl();
        SparkSession spark = SparkSession
                                 .builder()
                                 .appName("spark-jdbc-test")
                                 .master("local")
                                 .getOrCreate();
        Dataset<Row>
            rows =
            spark.read()
                .jdbc(connectionStr, genericSimpleInsertITest.getTableName(), new Properties());

        Properties properties = new Properties();
        properties.put("user", "default");

        String
            insertSQL =
            genericSimpleInsertITest.insertSQL()
                .replaceAll(genericSimpleInsertITest.getTableName(), "test");
        int colSize = genericSimpleInsertITest.getTypes().size();

        rows.foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
            withNewConnection(connection -> {
                PreparedStatement stmt = connection.prepareStatement(insertSQL);
                int count = 0;
                while (iterator.hasNext()) {
                    Row row = iterator.next();
                    for (int i = 0; i < colSize; i++) {
                        Object obj = row.get(i);
                        if (obj instanceof WrappedArray) {
                            WrappedArray array = (WrappedArray) obj;
                            Object[] objects = new Object[array.length()];
                            Iterator it = array.iterator();
                            int j = 0;
                            while (it.hasNext()) {
                                objects[j++] = it.next();
                            }
                            stmt.setObject(i + 1, objects);
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
            statement.executeQuery("TRUNCATE TABLE " + genericSimpleInsertITest.tableName);
            statement.executeQuery(
                "INSERT INTO " + genericSimpleInsertITest.tableName + " SELECT * FROM test");
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });

        // we do not check item, because orders are not guaranteed
        genericSimpleInsertITest.checkAggr();
        genericSimpleInsertITest.clean();
    }
}
