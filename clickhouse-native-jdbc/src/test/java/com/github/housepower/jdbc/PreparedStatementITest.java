package com.github.housepower.jdbc;

import org.junit.Test;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class PreparedStatementITest extends AbstractITest {

    @Test
    public void successfullyInt8Query() throws Exception {
        withNewConnection(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT ?,?");

            preparedStatement.setByte(1, (byte) 1);
            preparedStatement.setByte(2, (byte) 2);
            ResultSet rs = preparedStatement.executeQuery();
            assertTrue(rs.next());
            assertEquals(1, rs.getByte(1));
            assertEquals(2, rs.getByte(2));
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyInt16Query() throws Exception {
        withNewConnection(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT ?,?");

            preparedStatement.setShort(1, (short) 1);
            preparedStatement.setShort(2, (short) 2);
            ResultSet rs = preparedStatement.executeQuery();
            assertTrue(rs.next());
            assertEquals(1, rs.getShort(1));
            assertEquals(2, rs.getShort(2));
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyInt32Query() throws Exception {
        withNewConnection(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT ?,?");

            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, 2);
            ResultSet rs = preparedStatement.executeQuery();
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
            assertEquals(2, rs.getInt(2));
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyInt64Query() throws Exception {
        withNewConnection(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT ?,?");

            preparedStatement.setLong(1, 1);
            preparedStatement.setLong(2, 2);
            ResultSet rs = preparedStatement.executeQuery();
            assertTrue(rs.next());
            assertEquals(1, rs.getLong(1));
            assertEquals(2, rs.getLong(2));
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyStringQuery() throws Exception {
        withNewConnection(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT ?,?");

            preparedStatement.setString(1, "test1");
            preparedStatement.setString(2, "test2");
            ResultSet rs = preparedStatement.executeQuery();
            assertTrue(rs.next());
            assertEquals("test1", rs.getString(1));
            assertEquals("test2", rs.getString(2));
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyNullable() throws Exception {
        withNewConnection(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT arrayJoin([?,?])");

            preparedStatement.setString(1, null);
            preparedStatement.setString(2, "test2");
            ResultSet rs = preparedStatement.executeQuery();
            assertTrue(rs.next());
            assertNull(rs.getString(1));
            assertTrue(rs.wasNull());
            assertTrue(rs.next());
            assertEquals("test2", rs.getString(1));
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyDate() throws Exception {
        withNewConnection(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT toDate(?)");

            long now = System.currentTimeMillis();
            preparedStatement.setDate(1, new Date(now));
            ResultSet rs = preparedStatement.executeQuery();
            assertTrue(rs.next());
            assertEquals(now / TimeUnit.DAYS.toMillis(1),
                    rs.getDate(1).getTime() / TimeUnit.DAYS.toMillis(1));
            assertFalse(rs.next());
        }, true);
    }

    @Test
    public void successfullyInsertData() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            statement.execute("DROP TABLE IF EXISTS test");
            statement.execute("CREATE TABLE test(id UInt8, day Date, time DateTime)ENGINE = Log");

            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO test VALUES(?, ?, ?)");

            // 2018-07-01 19:00:00  Asia/Shanghai
            long time = 1530374400 + 19 * 3600;

            preparedStatement.setByte(1, (byte) 1);
            preparedStatement.setDate(2, new Date(time * 1000));
            preparedStatement.setTimestamp(3, new Timestamp(time * 1000));
            assertEquals(1, preparedStatement.executeUpdate());
        });
    }

}
