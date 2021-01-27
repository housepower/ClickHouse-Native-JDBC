/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.housepower.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.Struct;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class QueryComplexTypeITest extends AbstractITest {

    @Test
    public void successfullyDate() throws Exception {
        LocalDate date = LocalDate.of(2020, 1, 1);

        // use client timezone, Asia/Shanghai
        withStatement(statement -> {
            ResultSet rs = statement.executeQuery("select toDate('2020-01-01') as dateValue");
            assertTrue(rs.next());
            assertEquals(date, rs.getDate(1).toLocalDate());
            assertFalse(rs.next());
        }, "use_client_time_zone", true);

        // use server timezone, UTC
        withStatement(statement -> {
            ResultSet rs = statement.executeQuery("select toDate('2020-01-01') as dateValue");
            assertTrue(rs.next());
            assertEquals(date, rs.getDate(1).toLocalDate());
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyNullableWithDateTimeWithoutTimezone() throws Exception {
        withStatement(statement -> {
            long ts = 946659723 * 1000L;
            ResultSet rs = statement.executeQuery("SELECT nullIf(toDateTime(946659723), toDateTime(0))");
            assertTrue(rs.next());
            assertEquals(ts, rs.getTimestamp(1).getTime());
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyFixedString() throws Exception {
        withStatement(statement -> {
            ResultSet rs = statement.executeQuery("SELECT toFixedString('abc',3),toFixedString('abc',4)");

            assertTrue(rs.next());
            assertEquals("abc", rs.getString(1));
            assertEquals("abc\u0000", rs.getString(2));
        });
    }

    @Test
    public void successfullyNullableDataType() throws Exception {
        withStatement(statement -> {
            ResultSet rs = statement.executeQuery("SELECT arrayJoin([NULL,1])");

            assertTrue(rs.next());
            assertEquals(0, rs.getByte(1));
            assertTrue(rs.wasNull());
            assertTrue(rs.next());
            assertNotNull(rs.getObject(1));
        });
    }

    @Test
    public void successfullyNullableFixedStringType() throws Exception {
        withStatement(statement -> {
            ResultSet rs = statement.executeQuery("SELECT arrayJoin([NULL,toFixedString('abc',3)])");

            assertTrue(rs.next());
            assertNull(rs.getString(1));
            assertTrue(rs.wasNull());
            assertTrue(rs.next());
            assertEquals("abc", rs.getString(1));
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyArray() throws Exception {
        withStatement(statement -> {
            // Array(UInt8)
            ResultSet rs = statement.executeQuery("SELECT [[1], [2], [3], [4,5,6]] from numbers(10)");

            for (int i = 0; i < 10; i++) {
                assertTrue(rs.next());
                Array array1 = rs.getArray(1);
                Object[] objects = (Object[]) array1.getArray();
                assertEquals(4, objects.length);

                ClickHouseArray a1 = (ClickHouseArray) (objects[0]);
                ClickHouseArray a2 = (ClickHouseArray) (objects[1]);
                ClickHouseArray a3 = (ClickHouseArray) (objects[2]);
                ClickHouseArray a4 = (ClickHouseArray) (objects[3]);

                assertArrayEquals(new Short[]{(short) 1}, (Object[]) a1.getArray());
                assertArrayEquals(new Short[]{(short) 2}, (Object[]) a2.getArray());
                assertArrayEquals(new Short[]{(short) 3}, (Object[]) a3.getArray());
                assertArrayEquals(new Short[]{(short) 4, (short) 5, (short) 6}, (Object[]) a4.getArray());
            }
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyArrayJoin() throws Exception {
        withStatement(statement -> {
            // Array(UInt8)
            ResultSet rs = statement.executeQuery("SELECT arrayJoin([[1,2,3],[4,5]])");

            assertTrue(rs.next());
            Array array1 = rs.getArray(1);
            assertNotNull(array1);
            assertArrayEquals(new Short[]{(short) 1, (short) 2, (short) 3}, (Object[]) (array1.getArray()));

            assertTrue(rs.next());
            Array array2 = rs.getArray(1);
            assertNotNull(array2);
            assertArrayEquals(new Number[]{(short) 4, (short) 5}, (Object[]) array2.getArray());
        });
    }

    @Test
    public void successfullyArrayTuple() throws Exception {
        withStatement(statement -> {
            ResultSet rs = statement.executeQuery("SELECT arrayJoin([[(1,'3'), (2,'4')],[(3,'5')]])");

            assertTrue(rs.next());
            Array array1 = rs.getArray(1);
            assertNotNull(array1);

            Object[] row1 = (Object[]) array1.getArray();
            assertEquals(2, row1.length);
            assertEquals(1, ((Short) (((ClickHouseStruct) row1[0]).getAttributes()[0])).intValue());
            assertEquals("3", ((ClickHouseStruct) row1[0]).getAttributes()[1]);

            assertEquals(2, ((Short) (((ClickHouseStruct) row1[1]).getAttributes()[0])).intValue());
            assertEquals("4", ((ClickHouseStruct) row1[1]).getAttributes()[1]);

            assertTrue(rs.next());
            Array array2 = rs.getArray(1);
            Object[] row2 = (Object[]) array2.getArray();
            assertEquals(1, row2.length);
            assertEquals(3, ((Short) (((ClickHouseStruct) row2[0]).getAttributes()[0])).intValue());
            assertEquals("5", (((ClickHouseStruct) row2[0]).getAttributes()[1]));

            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyArrayArray() throws Exception {
        withStatement(statement -> {
            ResultSet rs = statement.executeQuery(
                    "SELECT [[1.1, 1.2], [2.1, 2.2], [3.1, 3.2]] AS v, toTypeName(v), [toNullable(10000), toNullable(10001)] from numbers(10)");

            for (int i = 0; i < 10; i++) {
                assertTrue(rs.next());
                Array array1 = rs.getArray(1);
                assertNotNull(array1);

                Double[][] res = new Double[][]{{1.1, 1.2}, {2.1, 2.2}, {3.1, 3.2}};

                Object[] arr = (Object[]) (rs.getArray(1).getArray());
                assertArrayEquals(res[0], (Object[]) ((ClickHouseArray) (arr[0])).getArray());
                assertArrayEquals(res[1], (Object[]) ((ClickHouseArray) (arr[1])).getArray());
                assertArrayEquals(res[2], (Object[]) ((ClickHouseArray) (arr[2])).getArray());
                assertEquals("Array(Array(Float64))", rs.getString(2));

                arr = (Object[]) (rs.getArray(3).getArray());
                assertEquals(10000, arr[0]);
                assertEquals(10001, arr[1]);
            }
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyTimestamp() throws Exception {
        withStatement(statement -> {
            long ts = 946659723 * 1000L;
            ResultSet rs = statement.executeQuery("SELECT toDateTime(946659723)");

            assertTrue(rs.next());
            assertEquals(ts, rs.getTimestamp(1).getTime());
        });
    }

    @Test
    public void successfullyNothing() throws Exception {
        withStatement(statement -> {
            ResultSet rs = statement.executeQuery("SELECT array()");
            assertTrue(rs.next());
            Array array = rs.getArray(1);
            assertEquals(array.getBaseTypeName(), "Nothing");
        });
    }

    @Test
    public void successfullyNullableNothing() throws Exception {
        withStatement(statement -> {
            ResultSet rs = statement.executeQuery("SELECT array(null)");
            assertTrue(rs.next());
            Array array = rs.getArray(1);
            assertEquals(array.getBaseTypeName(), "Nullable(Nothing)");
        });
    }

    @Test
    public void successfullyTuple() throws Exception {
        withStatement(statement -> {
            ResultSet rs = statement.executeQuery("SELECT (toUInt32(1),'2')");

            assertTrue(rs.next());
            Struct struct = (Struct) rs.getObject(1);
            assertArrayEquals(new Object[]{(long) 1, "2"}, struct.getAttributes());

            Map<String, Class<?>> attrNameWithClass = new LinkedHashMap<>();
            attrNameWithClass.put("_2", String.class);
            attrNameWithClass.put("_1", Long.class);
            assertArrayEquals(new Object[]{"2", (long) 1}, struct.getAttributes(attrNameWithClass));
        });
    }

    @Test
    public void successfullyEnum8() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.execute("CREATE TABLE test (test Enum8('a' = -1, 'b' = 1))ENGINE = Log");
            statement.execute("INSERT INTO test VALUES('a')");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");

            assertTrue(rs.next());
            assertEquals("a", rs.getString(1));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyEnum16() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.execute("CREATE TABLE test (test Enum16('a' = -1, 'b' = 1))ENGINE = Log");
            statement.execute("INSERT INTO test VALUES('a')");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");

            assertTrue(rs.next());
            assertEquals("a", rs.getString(1));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }
}
