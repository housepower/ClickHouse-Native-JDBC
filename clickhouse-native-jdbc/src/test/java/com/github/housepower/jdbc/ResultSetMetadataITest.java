package com.github.housepower.jdbc;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class ResultSetMetadataITest extends AbstractITest {

    @Test
    public void successfullyMetaData() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(a UInt8, b UInt64, c FixedString(3) )ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES (1, 2, '4' )");
                ResultSet rs = statement.executeQuery("SELECT * FROM test");
                ResultSetMetaData metadata =  rs.getMetaData();

                Assert.assertEquals(metadata.getTableName(1), "test");
                Assert.assertEquals(metadata.getCatalogName(1), "default");

                Assert.assertEquals(metadata.getPrecision(1), 3);
                Assert.assertEquals(metadata.getPrecision(2), 19);
                Assert.assertEquals(metadata.getPrecision(3), 3);
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }

}
