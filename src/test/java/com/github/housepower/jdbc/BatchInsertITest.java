package com.github.housepower.jdbc;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class BatchInsertITest extends AbstractITest {

    @Test
    public void successfullyBatchInsert() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.execute("DROP TABLE IF EXISTS test");
                statement.execute("CREATE TABLE test(id Int8, age UInt8, name String, name2 String)ENGINE=Log");
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO test VALUES(?, 1, ?, ?)");

                for (int i = 0; i < Byte.MAX_VALUE; i++) {
                    preparedStatement.setByte(1, (byte) i);
                    preparedStatement.setString(2, "Zhang San" + i);
                    preparedStatement.setString(3, "张三" + i);
                    preparedStatement.addBatch();
                }

                Assert.assertEquals(preparedStatement.executeBatch().length, Byte.MAX_VALUE);

                ResultSet rs = statement.executeQuery("select * from test");
                boolean hasResult = false;
                for (int i = 0; i < Byte.MAX_VALUE && rs.next(); i++) {
                    hasResult = true;
                    Assert.assertEquals(rs.getByte(1), i);
                    Assert.assertEquals(rs.getByte(2), 1);
                    Assert.assertEquals(rs.getString(3), "Zhang San" + i);
                    Assert.assertEquals(rs.getString(4), "张三" + i);
                }
                Assert.assertTrue(hasResult);
            }

        });

    }

    @Test
    public void successfullyMultipleBatchInsert() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.execute("DROP TABLE IF EXISTS test");
                statement.execute("CREATE TABLE test(id Int8, age UInt8, name String)ENGINE=Log");
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO test VALUES(?, 1, ?)");

                int insertBatchSize = 100;

                for (int i = 0; i < insertBatchSize; i++) {
                    preparedStatement.setByte(1, (byte) i);
                    preparedStatement.setString(2, "Zhang San" + i);
                    preparedStatement.addBatch();
                }

                Assert.assertEquals(preparedStatement.executeBatch().length, insertBatchSize);

                for (int i = 0; i < insertBatchSize; i++) {
                    preparedStatement.setByte(1, (byte) i);
                    preparedStatement.setString(2, "Zhang San" + i);
                    preparedStatement.addBatch();
                }

                Assert.assertEquals(preparedStatement.executeBatch().length, insertBatchSize);

                ResultSet rs = statement.executeQuery("select count(1) from test");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(2 * insertBatchSize, rs.getInt(1));
            }

        });

    }

    @Test
    public void successfullyNullableDataType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement stmt = connection.createStatement();
                int insertBatchSize = 100;

                stmt.executeQuery("DROP TABLE IF EXISTS test");
                stmt.executeQuery("create table test(day Date, name Nullable(String), name2 Nullable(FixedString(10)) ) Engine=Memory");
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO test VALUES(?, ?, ?)");
                for (int i = 0; i < insertBatchSize; i++) {
                    pstmt.setDate(1, new Date(System.currentTimeMillis()));

                    if (i % 2 == 0) {
                        pstmt.setString(2, "String");
                        pstmt.setString(3, "String");
                    } else {
                        pstmt.setString(2, null);
                        pstmt.setString(3, null);
                    }
                    pstmt.addBatch();
                }
                pstmt.executeBatch();

                ResultSet rs = stmt.executeQuery("select name, name2 from test order by name");
                int i = 0;
                while (rs.next()) {
                    String name1 = rs.getString(1);
                    String name2 = rs.getString(2);

                    if (i * 2 >= insertBatchSize) {
                        Assert.assertEquals(name1, null);
                        Assert.assertEquals(name1, null);
                    } else {
                        Assert.assertEquals(name1, "String");
                        Assert.assertTrue(name2.contains("String"));
                        Assert.assertTrue(name2.length() == 10);
                    }
                    i ++;
                }

                rs = stmt.executeQuery("select countIf(isNull(name)), countIf(isNotNull(name)), countIf(isNotNull(name2))  from test;");
                Assert.assertTrue(rs.next());
                Assert.assertEquals(insertBatchSize/2, rs.getInt(1));
                Assert.assertEquals(insertBatchSize/2, rs.getInt(2));
                Assert.assertEquals(insertBatchSize/2, rs.getInt(3));

                stmt.executeQuery("DROP TABLE IF EXISTS test");
            }
        });
    }

    @Test
    public void successfullyBatchInsertArray() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                System.setProperty("illegal-access", "allow");

                Statement statement = connection.createStatement();

                statement.execute("DROP TABLE IF EXISTS test");
                statement.execute("CREATE TABLE test(name Array(String), value Array(Float64), value2 Array(Array(Int32)))ENGINE=Log");
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO test VALUES(?, ?, [[1,2,3]])");

                List<String> array = Arrays.asList("aa", "bb", "cc");
                List<Double> array2 = Arrays.asList(1.2, 2.2, 3.2);

                for (int i = 0; i < Byte.MAX_VALUE; i++) {
                    preparedStatement.setArray(1, connection.createArrayOf("text", array.toArray()));
                    preparedStatement.setArray(2, connection.createArrayOf("text", array2.toArray()));

                    preparedStatement.addBatch();
                }

                Assert.assertEquals(preparedStatement.executeBatch().length, Byte.MAX_VALUE);

                ResultSet rs = statement.executeQuery("select * from test");
                while (rs.next()) {
                    Assert.assertArrayEquals((Object[]) rs.getArray(1).getArray(), array.toArray());
                    Assert.assertArrayEquals((Object[]) rs.getArray(2).getArray(), array2.toArray());
                }
            }
        });

    }

    @Test
    public void successfullyBatchInsertDateTime() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.execute("DROP TABLE IF EXISTS test");
                statement.execute("CREATE TABLE test(time DateTime)ENGINE=Log");
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO test VALUES(?)");

                // 2018-07-01 00:00:00  Asia/Shanghai
                long time = 1530374400;
                long insertTime = time;
                for (int i = 0; i < 24; i++) {
                    preparedStatement.setTimestamp(1, new Timestamp(insertTime * 1000));
                    preparedStatement.addBatch();
                    insertTime += 3600;
                }

                Assert.assertEquals(preparedStatement.executeBatch().length, 24);

                long selectTime = time;
                ResultSet rs = statement.executeQuery("SELECT  * FROM test ORDER BY time ASC");
                while (rs.next()) {
                    Assert.assertEquals(rs.getTimestamp(1).getTime(),
                            selectTime * 1000);
                    selectTime += 3600;
                }
            }
        });

    }
}
