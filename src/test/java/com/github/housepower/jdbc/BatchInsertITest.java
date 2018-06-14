package com.github.housepower.jdbc;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
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
                statement.execute("CREATE TABLE test(id UInt8, age UInt8, name String)ENGINE=Log");
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO test VALUES(?, 1, ?)");

                preparedStatement.setByte(1, (byte) 1);
                preparedStatement.setString(2, "Zhang San");
                preparedStatement.addBatch();
                preparedStatement.setByte(1, (byte) 2);
                preparedStatement.setString(2, "Li Si");
                preparedStatement.addBatch();
                Assert.assertArrayEquals(preparedStatement.executeBatch(), new int[] {-1, -1});
            }
        });

    }
}
