package com.github.housepower.jdbc;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class QueryComplexTypeITest extends AbstractITest {

    @Test
    public void successfullyNullableWithDateTimeWithoutTimezone() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();
                ResultSet rs =
                    statement.executeQuery("SELECT nullIf(toDateTime('2000-01-01 01:02:03'), toDateTime(0))");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getTimestamp(1).getTime(),
                    new Timestamp(2000 - 1900, 0, 1, 1, 2, 3, 0).getTime());
                Assert.assertFalse(rs.next());
            }
        });
    }

    @Test
    public void successfullyFixedString() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                ResultSet
                    rs =
                    statement.executeQuery("SELECT toFixedString('abc',3),toFixedString('abc',4)");

                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getString(1), "abc");
                Assert.assertEquals(rs.getString(2), "abc\u0000");
            }
        });
    }

    @Test
    public void successfullyNullableDataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                ResultSet rs = statement.executeQuery("SELECT arrayJoin([NULL,1])");

                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getByte(1), 0);
                Assert.assertTrue(rs.wasNull());
                Assert.assertTrue(rs.next());
                Assert.assertNotNull(rs.getObject(1));
            }
        });
    }

    @Test
    public void successfullyNullableFixedStringType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                ResultSet
                    rs =
                    statement.executeQuery("SELECT arrayJoin([NULL,toFixedString('abc',3)])");

                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getString(1), "\u0000\u0000\u0000");
                Assert.assertTrue(rs.wasNull());
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getString(1), "abc");
                Assert.assertFalse(rs.next());
            }
        });
    }

    @Test
    public void successfullyArray() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                ResultSet rs = statement.executeQuery("SELECT arrayJoin([[1,2,3],[4,5]])");

                Assert.assertTrue(rs.next());
                Array array1 = rs.getArray(1);
                Assert.assertNotNull(array1);
                Assert.assertArrayEquals((Byte[]) array1.getArray(), new Byte[]{1, 2, 3});
                Assert.assertTrue(rs.next());
                Array array2 = rs.getArray(1);
                Assert.assertNotNull(array2);
                Assert.assertArrayEquals((Byte[]) array2.getArray(), new Byte[]{4, 5});
            }
        });
    }

    @Test
    public void successfullyTimestamp() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT toDateTime('2000-01-01 01:02:03')");

                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getTimestamp(1).getTime(),
                    new Timestamp(2000 - 1900, 0, 1, 1, 2, 3, 0).getTime());
            }
        });
    }

    @Test
    public void successfullyTuple() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT (toUInt32(1),'2')");

                Assert.assertTrue(rs.next());
                Struct struct = (Struct) rs.getObject(1);
                Assert.assertEquals(struct.getAttributes(), new Object[]{1, "2"});

                Map<String, Class<?>> attrNameWithClass = new LinkedHashMap<String, Class<?>>();
                attrNameWithClass.put("_2", String.class);
                attrNameWithClass.put("_1", Integer.class);
                Assert.assertEquals(struct.getAttributes(attrNameWithClass), new Object[]{"2", 1});
            }
        });
    }

    @Test
    public void successfullyEnum8() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();
                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.execute("CREATE TABLE test (test Enum8('a' = -1, 'b' = 1))ENGINE = Log");
                statement.execute("INSERT INTO test VALUES('a')");
                ResultSet rs = statement.executeQuery("SELECT * FROM test");

                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getString(1), "a");
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }

    @Test
    public void successfullyEnum16() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();
                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.execute("CREATE TABLE test (test Enum16('a' = -1, 'b' = 1))ENGINE = Log");
                statement.execute("INSERT INTO test VALUES('a')");
                ResultSet rs = statement.executeQuery("SELECT * FROM test");

                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getString(1), "a");
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }
}
