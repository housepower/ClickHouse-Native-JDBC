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
                long ts = 946659723 * 1000L;
                ResultSet rs =
                    statement.executeQuery("SELECT nullIf(toDateTime(946659723), toDateTime(0))");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getTimestamp(1).getTime(), ts);
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

                // Array(UInt8)
                ResultSet rs = statement.executeQuery("SELECT arrayJoin([[1,2,3],[4,5]])");

                Assert.assertTrue(rs.next());
                Array array1 = rs.getArray(1);
                Assert.assertNotNull(array1);
                Assert.assertArrayEquals((Object[]) (array1.getArray()), new Short[]{ (short)1,  (short)2,  (short)3});
                Assert.assertTrue(rs.next());
                Array array2 = rs.getArray(1);
                Assert.assertNotNull(array2);
                Assert.assertArrayEquals((Object[]) array2.getArray(), new Number[]{ (short) 4, (short) 5});
            }
        });
    }

    @Test
    public void successfullyArrayTuple() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                ResultSet rs = statement.executeQuery("SELECT arrayJoin([[(1,'3'), (2,'4')],[(3,'5')]])");

                Assert.assertTrue(rs.next());
                Array array1 = rs.getArray(1);
                Assert.assertNotNull(array1);

                Object []row1 = (Object [])array1.getArray();
                Assert.assertTrue(row1.length == 2);
                Assert.assertEquals(((Short)(((ClickHouseStruct) row1[0]).getAttributes()[0])).intValue(), 1);
                Assert.assertEquals(((ClickHouseStruct) row1[0]).getAttributes()[1], "3");

                Assert.assertEquals(((Short)(((ClickHouseStruct) row1[1]).getAttributes()[0])).intValue(), 2);
                Assert.assertEquals(((ClickHouseStruct) row1[1]).getAttributes()[1], "4");

                Assert.assertTrue(rs.next());
                Array array2 = rs.getArray(1);
                Object []row2 = (Object [])array2.getArray();
                Assert.assertTrue(row2.length == 1);
                Assert.assertEquals(((Short)(((ClickHouseStruct) row2[0]).getAttributes()[0])).intValue(), 3);
                Assert.assertEquals((((ClickHouseStruct) row2[0]).getAttributes()[1]), "5");

                Assert.assertFalse(rs.next());
            }
        });
    }

    @Test
    public void successfullyTimestamp() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                long ts = 946659723 * 1000L;
                ResultSet rs = statement.executeQuery("SELECT toDateTime(946659723)");

                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getTimestamp(1).getTime(), ts);
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
                Assert.assertEquals(struct.getAttributes(), new Object[]{(long)1, "2"});

                Map<String, Class<?>> attrNameWithClass = new LinkedHashMap<String, Class<?>>();
                attrNameWithClass.put("_2", String.class);
                attrNameWithClass.put("_1", Long.class);
                Assert.assertEquals(struct.getAttributes(attrNameWithClass), new Object[]{"2", (long)1});
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
