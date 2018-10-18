package com.github.housepower.jdbc;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class InsertSimpleTypeITest extends AbstractITest {

    @Test
    public void successfullyInt8DataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test_uInt8 UInt8, test_Int8 Int8)ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES(" + 255 + "," + Byte.MIN_VALUE + ")");
                ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_uInt8");
                Assert.assertTrue(rs.next());
                int aa = rs.getInt(1);
                Assert.assertEquals(aa, 255);
                Assert.assertEquals(rs.getByte(2), Byte.MIN_VALUE);
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }

    @Test
    public void successfullyInt16DataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test_uInt16 UInt16, test_Int16 Int16)ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES(" + Short.MAX_VALUE + "," + Short.MIN_VALUE + ")");
                ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_uInt16");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getShort(1), Short.MAX_VALUE);
                Assert.assertEquals(rs.getShort(2), Short.MIN_VALUE);
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }

    @Test
    public void successfullyInt32DataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test_uInt32 UInt32, test_Int32 Int32)ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES(" + Integer.MAX_VALUE + "," + Integer.MIN_VALUE + ")");
                ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_uInt32");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getInt(1), Integer.MAX_VALUE);
                Assert.assertEquals(rs.getInt(2), Integer.MIN_VALUE);
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }

    @Test
    public void successfullyInt64DataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test_uInt64 UInt64, test_Int64 Int64)ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES(" + Long.MAX_VALUE + "," + Long.MIN_VALUE + ")");
                ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_uInt64");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getLong(1), Long.MAX_VALUE);
                Assert.assertEquals(rs.getLong(2), Long.MIN_VALUE);
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }

    @Test
    public void successfullyStringDataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test String)ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES('test_string')");
                ResultSet rs = statement.executeQuery("SELECT * FROM test");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getString(1), "test_string");
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }

    @Test
    public void successfullyDateDataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test Date, test2 Date)ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES('2000-01-01' , '2000-01-31')");
                ResultSet rs = statement.executeQuery("SELECT * FROM test");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getDate(1).getTime() / TimeUnit.DAYS.toMillis(1),
                    new Date(2000 - 1900, 0, 1).getTime() / TimeUnit.DAYS.toMillis(1));
                Assert.assertEquals(rs.getDate(2).getTime() / TimeUnit.DAYS.toMillis(1),
                                    new Date(2000 - 1900, 0, 31).getTime() / TimeUnit.DAYS.toMillis(1));

                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        }, true);
    }

    @Test
    public void successfullyFloatDataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test_float32 Float32)ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES(" + Float.MIN_VALUE + ")(" + Float.MAX_VALUE + ")");
                ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_float32");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getFloat(1), Float.MIN_VALUE, 0.000000000001);
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getFloat(1), Float.MAX_VALUE, 0.000000000001);
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }


    @Test
    public void successfullyDoubleDataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test_float64 Float64)ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES(" + Double.MIN_VALUE + ")(" + Double.MAX_VALUE + ")");
                ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_float64");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getDouble(1), Double.MIN_VALUE, 0.000000000001);
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getDouble(1), Double.MAX_VALUE, 0.000000000001);
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }

    @Test
    public void successfullyUUIDDataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(test UUID)ENGINE=Log");
                statement.executeQuery("INSERT INTO test VALUES('01234567-89ab-cdef-0123-456789abcdef')");
                ResultSet rs = statement.executeQuery("SELECT * FROM test");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getString(1), "01234567-89ab-cdef-0123-456789abcdef");
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }

    @Test
    public void successfullyMultipleValuesWithComma() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();
                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.execute("CREATE TABLE test(id Int32) ENGINE = Log");
                statement.execute("INSERT INTO test VALUES (1), (2)");
                ResultSet rs = statement.executeQuery("SELECT * FROM test");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getInt(1), 1);
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getInt(1), 2);
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }

    @Test
    public void successfullyUnsignedDataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.executeQuery("DROP TABLE IF EXISTS test");
                statement.executeQuery("CREATE TABLE test(i8 UInt8, i16 UInt16, i32 UInt32, i64 UInt64)ENGINE=Log");

                String insertSQL = "INSERT INTO test VALUES(" + ( (1 << 8) - 1) +
                                   "," + ( (1 << 16) - 1) +
                                   ",4294967295,-9223372036854775808)";

                statement.executeQuery(insertSQL);

                ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY i8");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getByte(1), -1 );
                Assert.assertEquals(rs.getShort(1), (1 << 8) - 1 );

                Assert.assertEquals(rs.getShort(2), -1 );
                Assert.assertEquals(rs.getInt(2), (1 << 16) - 1);

                Assert.assertEquals(rs.getInt(3), -1 );
                Assert.assertEquals(rs.getLong(3), 4294967295L);

                Assert.assertEquals(rs.getLong(4), -9223372036854775808L);
                Assert.assertEquals(rs.getBigDecimal(4), new BigDecimal("9223372036854775808"));
                Assert.assertFalse(rs.next());
                statement.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }
}
