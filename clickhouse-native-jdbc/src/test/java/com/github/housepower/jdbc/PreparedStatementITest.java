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

import com.github.housepower.misc.DateTimeUtil;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import static java.util.TimeZone.getTimeZone;
import static org.junit.jupiter.api.Assertions.*;

public class PreparedStatementITest extends AbstractITest {

    @Test
    public void successfullyInt8Query() throws Exception {
        withPreparedStatement("SELECT ?,?", pstmt -> {
            pstmt.setByte(1, (byte) 1);
            pstmt.setByte(2, (byte) 2);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(1, rs.getByte(1));
            assertEquals(2, rs.getByte(2));
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyInt16Query() throws Exception {
        withPreparedStatement("SELECT ?,?", pstmt -> {
            pstmt.setShort(1, (short) 1);
            pstmt.setShort(2, (short) 2);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(1, rs.getShort(1));
            assertEquals(2, rs.getShort(2));
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyInt32Query() throws Exception {
        withPreparedStatement("SELECT ?,?", pstmt -> {
            pstmt.setInt(1, 1);
            pstmt.setInt(2, 2);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
            assertEquals(2, rs.getInt(2));
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyInt64Query() throws Exception {
        withPreparedStatement("SELECT ?,?", pstmt -> {
            pstmt.setLong(1, 1);
            pstmt.setLong(2, 2);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(1, rs.getLong(1));
            assertEquals(2, rs.getLong(2));
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyStringQuery() throws Exception {
        withPreparedStatement("SELECT ?,?", pstmt -> {
            pstmt.setString(1, "test1");
            pstmt.setString(2, "test2");
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals("test1", rs.getString(1));
            assertEquals("test2", rs.getString(2));
            assertFalse(rs.next());
        });
    }

    @Test
    public void successfullyNullable() throws Exception {
        withPreparedStatement("SELECT arrayJoin([?,?])", pstmt -> {
            pstmt.setString(1, null);
            pstmt.setString(2, "test2");
            ResultSet rs = pstmt.executeQuery();
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

        LocalDateTime clientDateTime = DateTimeUtil.convertTimeZone(serverDateTime, SERVER_TZ, CLIENT_TZ);
        Calendar server_tz_cal = Calendar.getInstance(getTimeZone(SERVER_TZ), Locale.ROOT);
        Calendar client_tz_cal = Calendar.getInstance(getTimeZone(CLIENT_TZ), Locale.ROOT);
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
        withPreparedStatement("SELECT " +
                "toDate('" + dateLiteral + "'),               toDate(?),     toDate(?),     toDate(?), " +
                "toDateTime('" + serverDateTimeLiteral + "'), toDateTime(?), toDateTime(?), toDateTime(?)", pstmt -> {
            pstmt.setDate(1, Date.valueOf(date));
            pstmt.setString(2, dateLiteral);
            pstmt.setShort(3, (short) date.toEpochDay());
            pstmt.setTimestamp(4, Timestamp.valueOf(clientDateTime));
            pstmt.setString(5, serverDateTimeLiteral);
            pstmt.setLong(6, serverDateTime.atZone(SERVER_TZ).toEpochSecond());
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(date.toEpochDay(), rs.getDate(1).toLocalDate().toEpochDay());
            assertEquals(date.toEpochDay(), rs.getDate(2).toLocalDate().toEpochDay());
            assertEquals(date.toEpochDay(), rs.getDate(3).toLocalDate().toEpochDay());
            assertEquals(date.toEpochDay(), rs.getDate(4).toLocalDate().toEpochDay());
            assertEquals(clientDateTime, rs.getTimestamp(5).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(5, server_tz_cal).toLocalDateTime());
            assertEquals(serverDateTime, rs.getTimestamp(5, client_tz_cal).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(6).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(6, server_tz_cal).toLocalDateTime());
            assertEquals(serverDateTime, rs.getTimestamp(6, client_tz_cal).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(7).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(7, server_tz_cal).toLocalDateTime());
            assertEquals(serverDateTime, rs.getTimestamp(7, client_tz_cal).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(8).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(8, server_tz_cal).toLocalDateTime());
            assertEquals(serverDateTime, rs.getTimestamp(8, client_tz_cal).toLocalDateTime());
            assertFalse(rs.next());
        });

        // use client_time_zone
        withPreparedStatement("SELECT " +
                "toDate('" + dateLiteral + "'),               toDate(?),     toDate(?),     toDate(?), " +
                "toDateTime('" + serverDateTimeLiteral + "'), toDateTime(?), toDateTime(?), toDateTime(?)", pstmt -> {
            pstmt.setDate(1, Date.valueOf(date));
            pstmt.setString(2, dateLiteral);
            pstmt.setShort(3, (short) date.toEpochDay());
            pstmt.setTimestamp(4, Timestamp.valueOf(serverDateTime));
            pstmt.setString(5, serverDateTimeLiteral);
            pstmt.setLong(6, clientDateTime.atZone(CLIENT_TZ).toEpochSecond());
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(date.toEpochDay(), rs.getDate(1).toLocalDate().toEpochDay());
            assertEquals(date.toEpochDay(), rs.getDate(2).toLocalDate().toEpochDay());
            assertEquals(date.toEpochDay(), rs.getDate(3).toLocalDate().toEpochDay());
            assertEquals(date.toEpochDay(), rs.getDate(4).toLocalDate().toEpochDay());
            assertEquals(clientDateTime, rs.getTimestamp(5).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(5, server_tz_cal).toLocalDateTime());
            assertEquals(serverDateTime, rs.getTimestamp(5, client_tz_cal).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(6).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(6, server_tz_cal).toLocalDateTime());
            assertEquals(serverDateTime, rs.getTimestamp(6, client_tz_cal).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(7).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(7, server_tz_cal).toLocalDateTime());
            assertEquals(serverDateTime, rs.getTimestamp(7, client_tz_cal).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(8).toLocalDateTime());
            assertEquals(clientDateTime, rs.getTimestamp(8, server_tz_cal).toLocalDateTime());
            assertEquals(serverDateTime, rs.getTimestamp(8, client_tz_cal).toLocalDateTime());
            assertFalse(rs.next());
        }, "use_client_time_zone", true);
    }

    @Test
    public void successfullyInsertData() throws Exception {
        withStatement(stmt -> {

            stmt.execute("DROP TABLE IF EXISTS test");
            stmt.execute("CREATE TABLE test(" +
                    "id UInt8, " +
                    "day Date, " +
                    "time DateTime, " +
                    "flag Boolean" +
                    ")ENGINE = Log");

            withPreparedStatement(stmt.getConnection(), "INSERT INTO test VALUES(?, ?, ?, ?)", pstmt -> {
                // 2018-07-01 19:00:00  GMT
                // 2018-07-02 03:00:00  Asia/Shanghai
                long time = 1530403200 + 19 * 3600;
                // FIXME support setByte on UInt8
                pstmt.setByte(1, (byte) 1);
                pstmt.setDate(2, new Date(time * 1000));
                pstmt.setTimestamp(3, new Timestamp(time * 1000));
                pstmt.setBoolean(4, true);
                assertEquals(1, pstmt.executeUpdate());
            });
        });
    }
}
