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

import java.sql.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InsertComplexTypeITest extends AbstractITest {

    @Test
    public void successfullyArrayDataType() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test_Array Array(UInt8), test_Array2 Array(Array(String)))ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES ([1, 2, 3, 4], [ ['1', '2'] ])");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");
            assertTrue(rs.next());
            assertArrayEquals(new Short[]{1, 2, 3, 4}, (Object[]) rs.getArray(1).getArray());
            Object[] objects = (Object[]) rs.getArray(2).getArray();
            ClickHouseArray array = (ClickHouseArray) objects[0];
            assertArrayEquals(new Object[]{"1", "2"}, (Object[]) array.getArray());
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyFixedStringDataType() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(str FixedString(3))ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES('abc')");

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO test VALUES(?)");
            stmt.setObject(1, "abc");
            stmt.executeUpdate();

            ResultSet rs = statement.executeQuery("SELECT str, COUNT(0) FROM test group by str");
            assertTrue(rs.next());
            assertEquals("abc", rs.getString(1));
            assertEquals(2, rs.getInt(2));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyNullableDataType() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test_nullable Nullable(UInt8))ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES(Null)(1)(3)(Null)");
            ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_nullable");
            assertTrue(rs.next());
            assertEquals(1, rs.getByte(1));
            assertTrue(rs.next());
            assertEquals(3, rs.getByte(1));
            assertTrue(rs.next());
            assertEquals(0, rs.getByte(1));
            assertTrue(rs.wasNull());
            assertEquals(0, rs.getByte(1));
            assertTrue(rs.wasNull());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyDateTimeDataType() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test_datetime DateTime('UTC'), test_datetme2 DateTime('Asia/Shanghai') )ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES('2000-01-01 08:01:01', '2000-01-01 08:01:01')");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");
            assertTrue(rs.next());

            assertEquals(
                    Timestamp.valueOf(LocalDateTime.of(2000, 1, 1, 8, 1, 1, 0)).getTime(),
                    rs.getTimestamp(1).getTime());

            assertEquals(
                    Timestamp.valueOf(LocalDateTime.of(2000, 1, 1, 8, 1, 1, 0)).getTime(),
                    rs.getTimestamp(2).getTime());

            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        }, true);
    }

    @Test
    public void successfullyDateTime64DataType() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test_datetime DateTime64(9, 'UTC'))ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES(toDateTime64('2000-01-01 00:01:01.123456789'))");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");
            assertTrue(rs.next());

            assertEquals(
                    Timestamp.valueOf(LocalDateTime.of(2000, 1, 1, 0, 1, 1, 123456789)),
                    rs.getTimestamp(1));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        }, true);
    }

    @Test
    public void successfullyMinDateTime64DataType() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test_datetime DateTime64(9, 'UTC'))ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES(toDateTime64('1970-01-01 00:00:00.000000000'))");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");
            assertTrue(rs.next());
            assertEquals(
                    Timestamp.valueOf(LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0)),
                    rs.getTimestamp(1));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        }, true);
    }

    @Test
    public void successfullyMaxDateTime64DataType() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test_datetime DateTime64(9, 'UTC'))ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES(toDateTime64('2105-12-31 23:59:59.999999999'))");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");
            assertTrue(rs.next());

            assertEquals(
                    Timestamp.valueOf(LocalDateTime.of(2105, 12, 31, 23, 59, 59, 999999999)),
                    rs.getTimestamp(1));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        }, true);
    }

    @Test
    public void successfullyTupleDataType() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test_tuple Tuple(String, UInt8))ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES(('test_string', 1))");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");
            assertTrue(rs.next());
            assertArrayEquals(
                    new Object[]{"test_string", (short) (1)},
                    ((Struct) rs.getObject(1)).getAttributes());
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }
}
