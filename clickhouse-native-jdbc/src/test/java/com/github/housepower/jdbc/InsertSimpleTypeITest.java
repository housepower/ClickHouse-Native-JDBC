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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class InsertSimpleTypeITest extends AbstractITest {

    @Test
    public void successfullyInt8DataType() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test_uInt8 UInt8, test_Int8 Int8)ENGINE=Log");

            statement.executeQuery("INSERT INTO test VALUES(" + 255 + "," + Byte.MIN_VALUE + ")");

            ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_uInt8");
            assertTrue(rs.next());
            int aa = rs.getInt(1);
            assertEquals(255, aa);
            assertEquals(Byte.MIN_VALUE, rs.getByte(2));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyInt16DataType() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test_uInt16 UInt16, test_Int16 Int16)ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES(" + Short.MAX_VALUE + "," + Short.MIN_VALUE + ")");
            ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_uInt16");
            assertTrue(rs.next());
            assertEquals(Short.MAX_VALUE, rs.getShort(1));
            assertEquals(Short.MIN_VALUE, rs.getShort(2));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyInt32DataType() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test_uInt32 UInt32, test_Int32 Int32)ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES(" + Integer.MAX_VALUE + "," + Integer.MIN_VALUE + ")");
            ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_uInt32");
            assertTrue(rs.next());
            assertEquals(Integer.MAX_VALUE, rs.getInt(1));
            assertEquals(Integer.MIN_VALUE, rs.getInt(2));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyIPv4DataType() throws Exception {
        withStatement(statement -> {
            long minIp = 0L;
            long maxIp = (1L << 32) - 1;

            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(min_ip IPv4,max_ip IPv4)ENGINE=Log");

            withPreparedStatement(statement.getConnection(), "INSERT INTO test(min_ip, max_ip) VALUES(?, ?)", pstmt -> {
                for (int i = 0; i < 1; i++) {
                    pstmt.setLong(1, minIp);
                    pstmt.setLong(2, maxIp);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            });
            ResultSet rs = statement.executeQuery("SELECT min_ip,max_ip FROM test");
            assertTrue(rs.next());
            assertEquals(minIp, rs.getLong(1));
            assertEquals(maxIp, rs.getLong(2));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyInt64DataType() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test_uInt64 UInt64, test_Int64 Int64)ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES(" + Long.MAX_VALUE + "," + Long.MIN_VALUE + ")");
            ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_uInt64");
            assertTrue(rs.next());
            assertEquals(Long.MAX_VALUE, rs.getLong(1));
            assertEquals(Long.MIN_VALUE, rs.getLong(2));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyStringDataType() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test String)ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES('我爱祖国')");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");
            assertTrue(rs.next());
            assertEquals("我爱祖国", rs.getString(1));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyDateDataType() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test Date, test2 Date)ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES('2000-01-01', '2000-01-31')");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");
            assertTrue(rs.next());
            assertEquals(
                    LocalDate.of(2000, 1, 1).toEpochDay(),
                    rs.getDate(1).toLocalDate().toEpochDay());
            assertEquals(
                    LocalDate.of(2000, 1, 31).toEpochDay(),
                    rs.getDate(2).toLocalDate().toEpochDay());

            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        }, "use_client_time_zone", true);
    }

    @Test
    public void successfullyFloatDataType() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test_float32 Float32)ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES(" + Float.MIN_VALUE + ")(" + Float.MAX_VALUE + ")");
            ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_float32");
            assertTrue(rs.next());
            assertEquals(Float.MIN_VALUE, rs.getFloat(1), 0.000000000001);
            assertTrue(rs.next());
            assertEquals(Float.MAX_VALUE, rs.getFloat(1), 0.000000000001);
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }


    @Test
    public void successfullyDoubleDataType() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test_float64 Float64)ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES(" + Double.MIN_VALUE + ")(" + Double.MAX_VALUE + ")");
            ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_float64");
            assertTrue(rs.next());
            assertEquals(Double.MIN_VALUE, rs.getDouble(1), 0.000000000001);
            assertTrue(rs.next());
            assertEquals(Double.MAX_VALUE, rs.getDouble(1), 0.000000000001);
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyUUIDDataType() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test UUID)ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES('01234567-89ab-cdef-0123-456789abcdef')");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");
            assertTrue(rs.next());
            assertEquals("01234567-89ab-cdef-0123-456789abcdef", rs.getString(1));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyMultipleValuesWithComma() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.execute("CREATE TABLE test(id Int32) ENGINE = Log");
            statement.execute("INSERT INTO test VALUES (1), (2)");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
            assertTrue(rs.next());
            assertEquals(2, rs.getInt(1));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyUnsignedDataType() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(i8 UInt8, i16 UInt16, i32 UInt32, i64 UInt64)ENGINE=Log");

            String insertSQL = "INSERT INTO test VALUES(" + ((1 << 8) - 1) +
                    "," + ((1 << 16) - 1) +
                    ",4294967295,-9223372036854775808)";

            statement.executeQuery(insertSQL);

            ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY i8");
            assertTrue(rs.next());
            assertEquals(-1, rs.getByte(1));
            assertEquals((1 << 8) - 1, rs.getShort(1));

            assertEquals(-1, rs.getShort(2));
            assertEquals((1 << 16) - 1, rs.getInt(2));

            assertEquals(-1, rs.getInt(3));
            assertEquals(4294967295L, rs.getLong(3));

            assertEquals(-9223372036854775808L, rs.getLong(4));
            assertEquals(new BigDecimal("9223372036854775808"), rs.getBigDecimal(4));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyCharset() throws Exception {
        String[] charsets = new String[]{"UTF-8", "GB2312", "UTF-16"};
        for (String charset : charsets) {
            withStatement(statement -> {
                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(s1 String, s2 String)ENGINE=Log");

                String insertSQL = "INSERT INTO test VALUES('" + "我爱中国" +
                        "','" + "我爱地球" +
                        "')";

                statement.executeQuery(insertSQL);

                ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY s1");
                assertTrue(rs.next());
                assertEquals("我爱中国", rs.getString(1));
                assertEquals("我爱地球", rs.getString(2));

                assertArrayEquals("我爱中国".getBytes(charset), rs.getBytes(1));
                assertArrayEquals("我爱地球".getBytes(charset), rs.getBytes(2));

                assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }, "charset", charset);
        }
    }
}
