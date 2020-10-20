package com.github.housepower.jdbc;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Struct;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class QueryComplexTypeITest extends AbstractITest {

    @Test
    public void successfullyDate() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ROOT);

        // use client timezone, Asia/Shanghai in traivs-ci
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();
            ResultSet rs =
                statement.executeQuery("select toDate('2020-01-01') as dateValue");
            Assert.assertTrue(rs.next());

            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            java.util.Date date = dateFormat.parse("2020-01-01 08:00:00");
            Assert.assertEquals(rs.getDate(1).getTime(), date.getTime());
            Assert.assertFalse(rs.next());
        }, true);

        // use server timezone, UTC
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();
            ResultSet rs =
                statement.executeQuery("select toDate('2020-01-01') as dateValue");
            Assert.assertTrue(rs.next());

            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            java.util.Date date = dateFormat.parse("2020-01-01 00:00:00");
            Assert.assertEquals(rs.getDate(1).getTime(), date.getTime());
            Assert.assertFalse(rs.next());
        }, false);
    }

    @Test
    public void successfullyNullableWithDateTimeWithoutTimezone() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();
            long ts = 946659723 * 1000L;
            ResultSet rs =
                statement.executeQuery("SELECT nullIf(toDateTime(946659723), toDateTime(0))");
            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getTimestamp(1).getTime(), ts);
            Assert.assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyFixedString() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            ResultSet
                rs =
                statement.executeQuery("SELECT toFixedString('abc',3),toFixedString('abc',4)");

            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getString(1), "abc");
            Assert.assertEquals(rs.getString(2), "abc\u0000");
        });
    }

    @Test
    public void successfullyNullableDataType() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("SELECT arrayJoin([NULL,1])");

            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getByte(1), 0);
            Assert.assertTrue(rs.wasNull());
            Assert.assertTrue(rs.next());
            Assert.assertNotNull(rs.getObject(1));
        });
    }

    @Test
    public void successfullyNullableFixedStringType() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            ResultSet
                rs =
                statement.executeQuery("SELECT arrayJoin([NULL,toFixedString('abc',3)])");

            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getString(1), null);
            Assert.assertTrue(rs.wasNull());
            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getString(1), "abc");
            Assert.assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyArray() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            // Array(UInt8)
            ResultSet rs = statement.executeQuery("SELECT [[1], [2], [3], [4,5,6]] from numbers(10)");

            for (int i = 0; i < 10; i ++) {
                Assert.assertTrue(rs.next());
                Array array1 = rs.getArray(1);
                Object[] objects = (Object[]) array1.getArray();
                Assert.assertEquals(objects.length, 4);

                ClickHouseArray a1 = (ClickHouseArray)(objects[0]);
                ClickHouseArray a2 = (ClickHouseArray)(objects[1]);
                ClickHouseArray a3 = (ClickHouseArray)(objects[2]);
                ClickHouseArray a4 = (ClickHouseArray)(objects[3]);

                Assert.assertArrayEquals((Object[]) a1.getArray(), new Short[]{(short) 1});
                Assert.assertArrayEquals((Object[]) a2.getArray(), new Short[]{(short) 2});
                Assert.assertArrayEquals((Object[]) a3.getArray(), new Short[]{(short) 3});
                Assert.assertArrayEquals((Object[]) a4.getArray(), new Short[]{(short) 4, (short)5, (short)6});
            }
            Assert.assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyArrayJoin() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            // Array(UInt8)
            ResultSet rs = statement.executeQuery("SELECT arrayJoin([[1,2,3],[4,5]])");

            Assert.assertTrue(rs.next());
            Array array1 = rs.getArray(1);
            Assert.assertNotNull(array1);
            Assert.assertArrayEquals((Object[]) (array1.getArray()),
                                     new Short[]{(short) 1, (short) 2, (short) 3});

            Assert.assertTrue(rs.next());
            Array array2 = rs.getArray(1);
            Assert.assertNotNull(array2);
            Assert.assertArrayEquals((Object[]) array2.getArray(),
                                     new Number[]{(short) 4, (short) 5});
        });
    }

    @Test
    public void successfullyArrayTuple() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            ResultSet
                rs =
                statement.executeQuery("SELECT arrayJoin([[(1,'3'), (2,'4')],[(3,'5')]])");

            Assert.assertTrue(rs.next());
            Array array1 = rs.getArray(1);
            Assert.assertNotNull(array1);

            Object[] row1 = (Object[]) array1.getArray();
            Assert.assertTrue(row1.length == 2);
            Assert.assertEquals(
                ((Short) (((ClickHouseStruct) row1[0]).getAttributes()[0])).intValue(), 1);
            Assert.assertEquals(((ClickHouseStruct) row1[0]).getAttributes()[1], "3");

            Assert.assertEquals(
                ((Short) (((ClickHouseStruct) row1[1]).getAttributes()[0])).intValue(), 2);
            Assert.assertEquals(((ClickHouseStruct) row1[1]).getAttributes()[1], "4");

            Assert.assertTrue(rs.next());
            Array array2 = rs.getArray(1);
            Object[] row2 = (Object[]) array2.getArray();
            Assert.assertTrue(row2.length == 1);
            Assert.assertEquals(
                ((Short) (((ClickHouseStruct) row2[0]).getAttributes()[0])).intValue(), 3);
            Assert.assertEquals((((ClickHouseStruct) row2[0]).getAttributes()[1]), "5");

            Assert.assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyArrayArray() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            ResultSet
                rs =
                statement.executeQuery(
                    "SELECT [[1.1, 1.2], [2.1, 2.2], [3.1, 3.2]] AS v, toTypeName(v) from numbers(10)");

            for (int i = 0; i < 10; i++) {
                Assert.assertTrue(rs.next());
                Array array1 = rs.getArray(1);
                Assert.assertNotNull(array1);

                Double[][] res = new Double[][]{{1.1, 1.2}, {2.1, 2.2}, {3.1, 3.2}};

                Object[] arr = (Object[]) (rs.getArray(1).getArray());
                Assert.assertArrayEquals((Object[]) ((ClickHouseArray) (arr[0])).getArray(),
                                         res[0]);
                Assert.assertArrayEquals((Object[]) ((ClickHouseArray) (arr[1])).getArray(),
                                         res[1]);
                Assert.assertArrayEquals((Object[]) ((ClickHouseArray) (arr[2])).getArray(),
                                         res[2]);
                Assert.assertEquals(rs.getString(2), "Array(Array(Float64))");
            }
            Assert.assertFalse(rs.next());

        });
    }

    @Test
    public void successfullyTimestamp() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            long ts = 946659723 * 1000L;
            ResultSet rs = statement.executeQuery("SELECT toDateTime(946659723)");

            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getTimestamp(1).getTime(), ts);
        });
    }

    @Test
    public void successfullyTuple() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT (toUInt32(1),'2')");

            Assert.assertTrue(rs.next());
            Struct struct = (Struct) rs.getObject(1);
            Assert.assertEquals(struct.getAttributes(), new Object[]{(long) 1, "2"});

            Map<String, Class<?>> attrNameWithClass = new LinkedHashMap<String, Class<?>>();
            attrNameWithClass.put("_2", String.class);
            attrNameWithClass.put("_1", Long.class);
            Assert.assertEquals(struct.getAttributes(attrNameWithClass),
                                new Object[]{"2", (long) 1});
        });
    }

    @Test
    public void successfullyEnum8() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.execute("CREATE TABLE test (test Enum8('a' = -1, 'b' = 1))ENGINE = Log");
            statement.execute("INSERT INTO test VALUES('a')");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");

            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getString(1), "a");
            Assert.assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyEnum16() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.execute("CREATE TABLE test (test Enum16('a' = -1, 'b' = 1))ENGINE = Log");
            statement.execute("INSERT INTO test VALUES('a')");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");

            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getString(1), "a");
            Assert.assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }
}
