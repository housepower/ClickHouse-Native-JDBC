package com.github.housepower.jdbc;

import org.junit.Assert;
import org.junit.Test;

import java.sql.*;

public class InsertComplexTypeITest extends AbstractITest {

    @Test
    public void successfullyArrayDataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test_Array Array(UInt8), test_Array2 Array(Array(String)))ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES ([1, 2, 3, 4], [ ['1', '2'] ])");
                ResultSet rs = statement.executeQuery("SELECT * FROM test");
                Assert.assertTrue(rs.next());
                Assert.assertArrayEquals((Object[]) rs.getArray(1).getArray(), new Short[] {1, 2, 3, 4});
                Object[] objects = (Object[]) rs.getArray(2).getArray();
                ClickHouseArray array = (ClickHouseArray) objects[0];
                Assert.assertArrayEquals((Object [])array.getArray(), new Object[]{"1","2"});
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
                stmt.setObject(1, "abc");
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
                statement.executeQuery("INSERT INTO test VALUES(Null)(1)(3)(Null)");
                ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_nullable");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getByte(1), 1);
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getByte(1), 3);
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getByte(1), 0);
                Assert.assertTrue(rs.wasNull());
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
    public void successfullyDateTime64DataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test_datetime DateTime64(9, 'UTC'))ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES(toDateTime64('2000-01-01 00:01:01.123456789'))");
                ResultSet rs = statement.executeQuery("SELECT * FROM test");
                Assert.assertTrue(rs.next());

                Assert.assertEquals(new Timestamp(2000 - 1900, 0, 1, 0, 1, 1, 123456789),
                        rs.getTimestamp(1));
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        }, true);
    }

    @Test
    public void successfullyMinDateTime64DataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test_datetime DateTime64(9, 'UTC'))ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES(toDateTime64('1970-01-01 00:00:00.000000000'))");
                ResultSet rs = statement.executeQuery("SELECT * FROM test");
                Assert.assertTrue(rs.next());

                Assert.assertEquals(new Timestamp(1970 - 1900, 0, 1, 0, 0, 0, 0),
                        rs.getTimestamp(1));
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        }, true);
    }

    @Test
    public void successfullyMaxDateTime64DataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test_datetime DateTime64(9, 'UTC'))ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES(toDateTime64('2105-12-31 23:59:59.999999999'))");
                ResultSet rs = statement.executeQuery("SELECT * FROM test");
                Assert.assertTrue(rs.next());

                Assert.assertEquals(new Timestamp(2105 - 1900, 11, 31, 23, 59, 59, 999999999),
                        rs.getTimestamp(1));
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
