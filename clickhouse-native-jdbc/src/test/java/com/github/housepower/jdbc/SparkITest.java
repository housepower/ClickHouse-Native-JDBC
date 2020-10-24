package com.github.housepower.jdbc;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;

import java.sql.Statement;
import java.util.Properties;

public class SparkITest extends AbstractITest {


    // insert to genericSimpleInsertITest
    // spark insert into test from genericSimpleInsertITest
    // truncate genericSimpleInsertITest
    // insert into genericSimpleInsertITest from test
    // genericSimpleInsertITest check
    @Test
    public void successfullySparks() throws Exception {
        GenericSimpleInsertITest genericSimpleInsertITest = new GenericSimpleInsertITest();

        genericSimpleInsertITest.clean();
        genericSimpleInsertITest.create();
        genericSimpleInsertITest.insert();

        withNewConnection(connection -> {
            Statement statement = connection.createStatement();
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test as " + genericSimpleInsertITest.tableName + " Engine = Memory");
        });

        String connectionStr = "jdbc:clickhouse://127.0.0.1:" + System.getProperty("CLICK_HOUSE_SERVER_PORT", "9000");
        SparkSession spark = SparkSession
                                 .builder()
                                 .appName("spark-jdbc-test")
                                 .master("local")
                                 .getOrCreate();
        Dataset<Row> row = spark.read().jdbc(connectionStr, genericSimpleInsertITest.getTableName() , new Properties());

        Properties properties = new Properties();
        properties.put("user", "default");
        row.write().mode("append")
            .option("driver", "com.github.housepower.jdbc.ClickHouseDriver")
            .option("user", "default")
            .option("batchsize", "65536")
            .option("isolationLevel", "NONE")
            .jdbc(connectionStr, "default.test", properties);


        withNewConnection(connection -> {
            Statement statement = connection.createStatement();
            statement.executeQuery("TRUNCATE TABLE " + genericSimpleInsertITest.tableName);
            statement.executeQuery("INSERT INTO " + genericSimpleInsertITest.tableName + " SELECT * FROM test");
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
        genericSimpleInsertITest.check();
        genericSimpleInsertITest.clean();
    }
}
