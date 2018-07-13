package com.github.housepower.jdbc;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class BatchInsertITest extends AbstractITest {

    @Test
    public void successfullyBatchInsert() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.execute("DROP TABLE IF EXISTS test");
                statement.execute("CREATE TABLE test(id Int8, age UInt8, name String)ENGINE=Log");
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO test VALUES(?, 1, ?)");

                for (int i = 0; i < Byte.MAX_VALUE; i++) {
                    preparedStatement.setByte(1, (byte) i);
                    preparedStatement.setString(2, "Zhang San" + i);
                    preparedStatement.addBatch();
                }

                Assert.assertEquals(preparedStatement.executeBatch().length, Byte.MAX_VALUE);
            }
        });

    }

    @Test
    public void successfullyBatchInsertDateTime() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.execute("DROP TABLE IF EXISTS test");
                statement.execute("CREATE TABLE test(time DateTime)ENGINE=Log");
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO test VALUES(?)");

                // 2018-07-01 00:00:00  Asia/Shanghai
                long time = 1530374400 ;
                long insertTime = time;
                for (int i = 0; i < 24; i++) {
                    preparedStatement.setTimestamp(1, new Timestamp(insertTime * 1000));
                    preparedStatement.addBatch();
                    insertTime += 3600;
                }

                Assert.assertEquals(preparedStatement.executeBatch().length, 24);

                long selectTime = time;
                ResultSet rs = statement.executeQuery("SELECT  * FROM test ORDER BY time ASC");
                while (rs.next()) {
                    Assert.assertEquals(rs.getTimestamp(1).getTime(),
                                        selectTime * 1000);
                    selectTime += 3600;
                }
            }
        });

    }
}
