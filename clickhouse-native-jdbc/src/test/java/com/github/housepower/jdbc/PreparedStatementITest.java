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

import com.github.housepower.jdbc.misc.DateTimeHelper;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

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
    public void successfullyDateIndependentWithTz() throws Exception {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT);
        DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
        LocalDate date = LocalDate.of(2020, 11, 7);
        LocalDateTime serverDateTime = date.atTime(9, 43, 12);

        LocalDateTime clientDateTime = DateTimeHelper.convertTimeZone(serverDateTime, SERVER_TZ, CLIENT_TZ);
        String dateLiteral = date.format(dateFmt);
        String clientDateTimeLiteral = clientDateTime.format(dateTimeFmt);
        String serverDateTimeLiteral = serverDateTime.format(dateTimeFmt);
        assertEquals(18573, date.toEpochDay());
        assertEquals("2020-11-07", dateLiteral);
        assertEquals(1604742192, clientDateTime.atZone(CLIENT_TZ).toEpochSecond());
        assertEquals(1604742192, serverDateTime.atZone(SERVER_TZ).toEpochSecond());
        // if client_time_zone is Asia/Shanghai
        // assertEquals("2020-11-07 17:43:12", clientDateTimeLiteral);
        assertEquals("2020-11-07 09:43:12", serverDateTimeLiteral);

        // use server_time_zone
        withNewConnection(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " +
                    "toDate('" + dateLiteral + "'),               toDate(?),     toDate(?),     toDate(?), " +
                    "toDateTime('" + serverDateTimeLiteral + "'), toDateTime(?), toDateTime(?), toDateTime(?)");
            preparedStatement.setDate(1, Date.valueOf(date));
            preparedStatement.setString(2, dateLiteral);
            preparedStatement.setShort(3, (short) date.toEpochDay());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(clientDateTime));
            preparedStatement.setString(5, serverDateTimeLiteral);
            preparedStatement.setLong(6, serverDateTime.atZone(SERVER_TZ).toEpochSecond());
            ResultSet rs = preparedStatement.executeQuery();
            assertTrue(rs.next());
            assertEquals(date.toEpochDay(), rs.getDate(1).toLocalDate().toEpochDay());
            assertEquals(date.toEpochDay(), rs.getDate(2).toLocalDate().toEpochDay());
            assertEquals(date.toEpochDay(), rs.getDate(3).toLocalDate().toEpochDay());
            assertEquals(date.toEpochDay(), rs.getDate(4).toLocalDate().toEpochDay());
            assertEquals(clientDateTime, rs.getTimestamp(5).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(6).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(7).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(8).toLocalDateTime());
            assertFalse(rs.next());
        });

        // use client_time_zone
        withNewConnection(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " +
                    "toDate('" + dateLiteral + "'),               toDate(?),     toDate(?),     toDate(?), " +
                    "toDateTime('" + serverDateTimeLiteral + "'), toDateTime(?), toDateTime(?), toDateTime(?)");
            preparedStatement.setDate(1, Date.valueOf(date));
            preparedStatement.setString(2, dateLiteral);
            preparedStatement.setShort(3, (short) date.toEpochDay());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(serverDateTime));
            preparedStatement.setString(5, serverDateTimeLiteral);
            preparedStatement.setLong(6, clientDateTime.atZone(CLIENT_TZ).toEpochSecond());
            ResultSet rs = preparedStatement.executeQuery();
            assertTrue(rs.next());
            assertEquals(date.toEpochDay(), rs.getDate(1).toLocalDate().toEpochDay());
            assertEquals(date.toEpochDay(), rs.getDate(2).toLocalDate().toEpochDay());
            assertEquals(date.toEpochDay(), rs.getDate(3).toLocalDate().toEpochDay());
            assertEquals(date.toEpochDay(), rs.getDate(4).toLocalDate().toEpochDay());
            assertEquals(clientDateTime, rs.getTimestamp(5).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(6).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(7).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(8).toLocalDateTime());
            assertFalse(rs.next());
        }, "use_client_time_zone", true);
    }

    @Test
    public void successfullyInsertData() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            statement.execute("DROP TABLE IF EXISTS test");
            statement.execute("CREATE TABLE test(" +
                    "id UInt8, " +
                    "day Date, " +
                    "time DateTime, " +
                    "flag Boolean" +
                    ")ENGINE = Log");

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO test VALUES(?, ?, ?, ?)");

            // 2018-07-01 19:00:00  GMT
            // 2018-07-02 03:00:00  Asia/Shanghai
            long time = 1530403200 + 19 * 3600;

            preparedStatement.setByte(1, (byte) 1);
            preparedStatement.setDate(2, new Date(time * 1000));
            preparedStatement.setTimestamp(3, new Timestamp(time * 1000));
            preparedStatement.setBoolean(4, true);
            assertEquals(1, preparedStatement.executeUpdate());
        });
    }

}
