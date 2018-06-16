package com.github.housepower.jdbc;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

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
}
