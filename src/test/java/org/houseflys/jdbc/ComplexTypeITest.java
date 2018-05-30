package org.houseflys.jdbc;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

public class ComplexTypeITest extends AbstractITest {

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
                Assert.assertNull(rs.getObject(1));
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
                Assert.assertNull(rs.getObject(1));
                Assert.assertTrue(rs.next());
                Assert.assertNotNull(rs.getObject(1));
                Assert.assertEquals(rs.getString(1), "abc");
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
                Assert.assertEquals(rs.getTimestamp(1).getTime(), 946659723000L);
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
    public void successfullyEnum() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();
                try {
                    statement.executeQuery(
                        "CREATE TABLE `_enum_test` ( a Enum8('a' = 1, 'b' = 2), b Enum16('str\\'(,)_aa' = 32767, 'f\\'(,))\\'' = 12345) ) ENGINE = Memory");
                    statement.executeQuery(
                        "INSERT INTO `_enum_test` SELECT number % 2 + 1 , if(number % 2 = 0, 32767, 12345) from numbers(100)");

                    ResultSet rs = statement.executeQuery("SELECT * FROM `_enum_test`");

                    int i = 0;
                    while (rs.next()) {
                        String name1 = rs.getString(1);
                        String name2 = rs.getString(2);
                        if (i % 2 == 0) {
                            Assert.assertEquals(name1, "a");
                            Assert.assertEquals(name2, "str\\'(,)_aa");
                        } else {
                            Assert.assertEquals(name1, "b");
                            Assert.assertEquals(name2, "f\\'(,))\\'");
                        }
                        i++;
                    }
                    Assert.assertEquals(i, 100);
                } finally {
                    statement.executeQuery("DROP TABLE IF EXISTS `_enum_test`");
                }
            }
        });
    }
}
