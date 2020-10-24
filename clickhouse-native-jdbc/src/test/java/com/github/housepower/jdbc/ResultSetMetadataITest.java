package com.github.housepower.jdbc;

import org.junit.Test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import static org.junit.Assert.*;

public class ResultSetMetadataITest extends AbstractITest {

    @Test
    public void successfullyMetaData() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(a UInt8, b UInt64, c FixedString(3) )ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES (1, 2, '4' )");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");
            ResultSetMetaData metadata =  rs.getMetaData();

            assertEquals("test", metadata.getTableName(1));
            assertEquals("default", metadata.getCatalogName(1));

            assertEquals(3, metadata.getPrecision(1));
            assertEquals(19, metadata.getPrecision(2));
            assertEquals(3, metadata.getPrecision(3));
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

}
