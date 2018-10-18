package com.github.housepower.jdbc;

import org.junit.Assert;
import org.junit.Test;

import java.sql.*;
import java.util.TimeZone;

public class InsertComplexTypeITest extends AbstractITest {

    @Test
    public void successfullyArrayDataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test_Array Array(UInt8))ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES ([1, 2, 3, 4])");
                ResultSet rs = statement.executeQuery("SELECT * FROM test");
                Assert.assertTrue(rs.next());
                Assert.assertArrayEquals((Object[]) rs.getArray(1).getArray(), new Short[] {1, 2, 3, 4});
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }

    @Test
    public void successfullyFixedStringDataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(str FixedString(3))ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES('abc')");

                PreparedStatement stmt = connection.prepareStatement("INSERT INTO test VALUES(?)");
                stmt.setString(1, "abc");
                stmt.executeUpdate();

                ResultSet rs = statement.executeQuery("SELECT str, COUNT(0) FROM test group by str");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getString(1), "abc");
                Assert.assertEquals(rs.getInt(2), 2);
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }

    @Test
    public void successfullyNullableDataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test_nullable Nullable(UInt8))ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES(Null)(1)");
                ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_nullable");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getByte(1), 1);
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getByte(1), 0);
                Assert.assertTrue(rs.wasNull());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }

    @Test
    public void successfullyDateTimeDataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test_datetime DateTime)ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES('2000-01-01 00:01:01')");
                ResultSet rs = statement.executeQuery("SELECT * FROM test");
                Assert.assertTrue(rs.next());

                Assert.assertEquals(rs.getTimestamp(1).getTime(),
                    new Timestamp(2000 - 1900, 0, 1, 0, 1, 1, 0).getTime());
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        }, true);
    }

    @Test
    public void successfullyTupleDataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test_tuple Tuple(String, UInt8))ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES(('test_string', 1))");
                ResultSet rs = statement.executeQuery("SELECT * FROM test");
                Assert.assertTrue(rs.next());
                Assert.assertArrayEquals(((Struct) rs.getObject(1)).getAttributes(), new Object[] {"test_string", (short)(1) });
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }
}
