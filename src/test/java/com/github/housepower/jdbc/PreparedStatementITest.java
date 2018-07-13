package com.github.housepower.jdbc;

import org.junit.Assert;
import org.junit.Test;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PreparedStatementITest extends AbstractITest {

    @Test
    public void successfullyInt8Query() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT ?,?");

                preparedStatement.setByte(1, (byte) 1);
                preparedStatement.setByte(2, (byte) 2);
                ResultSet rs = preparedStatement.executeQuery();
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getByte(1), 1);
                Assert.assertEquals(rs.getByte(2), 2);
                Assert.assertFalse(rs.next());
            }
        });
    }

    @Test
    public void successfullyInt16Query() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT ?,?");

                preparedStatement.setShort(1, (short) 1);
                preparedStatement.setShort(2, (short) 2);
                ResultSet rs = preparedStatement.executeQuery();
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getShort(1), 1);
                Assert.assertEquals(rs.getShort(2), 2);
                Assert.assertFalse(rs.next());
            }
        });
    }

    @Test
    public void successfullyInt32Query() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT ?,?");

                preparedStatement.setInt(1, 1);
                preparedStatement.setInt(2, 2);
                ResultSet rs = preparedStatement.executeQuery();
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getInt(1), 1);
                Assert.assertEquals(rs.getInt(2), 2);
                Assert.assertFalse(rs.next());
            }
        });
    }

    @Test
    public void successfullyInt64Query() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT ?,?");

                preparedStatement.setLong(1, 1);
                preparedStatement.setLong(2, 2);
                ResultSet rs = preparedStatement.executeQuery();
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getLong(1), 1);
                Assert.assertEquals(rs.getLong(2), 2);
                Assert.assertFalse(rs.next());
            }
        });
    }

    @Test
    public void successfullyStringQuery() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT ?,?");

                preparedStatement.setString(1, "test1");
                preparedStatement.setString(2, "test2");
                ResultSet rs = preparedStatement.executeQuery();
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getString(1), "test1");
                Assert.assertEquals(rs.getString(2), "test2");
                Assert.assertFalse(rs.next());
            }
        });
    }

    @Test
    public void successfullyNullable() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT arrayJoin([?,?])");

                preparedStatement.setString(1, null);
                preparedStatement.setString(2, "test2");
                ResultSet rs = preparedStatement.executeQuery();
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getString(1), "");
                Assert.assertTrue(rs.wasNull());
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getString(1), "test2");
                Assert.assertFalse(rs.next());
            }
        });
    }

    @Test
    public void successfullyDate() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT toDate(?)");

                long now = System.currentTimeMillis();
                preparedStatement.setDate(1, new Date(now));
                ResultSet rs = preparedStatement.executeQuery();
                Assert.assertTrue(rs.next());
//                Assert.assertEquals(rs.getDate(1).getTime() / TimeUnit.DAYS.toMillis(1),
//                    now / TimeUnit.DAYS.toMillis(1) - 1);
                Assert.assertFalse(rs.next());
            }
        }, true);
    }

    @Test
    public void successfullyInsertData() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                Statement statement = connection.createStatement();

                statement.execute("DROP TABLE IF EXISTS test");
                statement.execute("CREATE TABLE test(id UInt8, day Date, time DateTime)ENGINE = Log");

                PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO test VALUES(?, ?, ?)");

                // 2018-07-01 19:00:00  Asia/Shanghai
                long time = 1530374400 + 19 * 3600;

                preparedStatement.setByte(1, (byte)1);
                preparedStatement.setDate(2, new Date(time * 1000));
                preparedStatement.setTimestamp(3, new Timestamp(time * 1000));
                Assert.assertEquals(preparedStatement.executeUpdate(), 1);
            }
        });
    }

}
