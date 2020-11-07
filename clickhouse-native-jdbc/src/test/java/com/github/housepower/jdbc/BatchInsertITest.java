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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BatchInsertITest extends AbstractITest {

    void assertBatchInsertResult(int[] result, int expectedRowCount) {
        assertEquals(expectedRowCount, result.length);
        assertEquals(expectedRowCount, Arrays.stream(result).sum());
    }

    @Test
    public void successfullyBatchInsert() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            statement.execute("DROP TABLE IF EXISTS test");
            statement.execute("CREATE TABLE test(id Int8, age UInt8, name String, name2 String)ENGINE=Log");
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO test VALUES(?, 1, ?, ?)");

            for (int i = 0; i < Byte.MAX_VALUE; i++) {
                preparedStatement.setByte(1, (byte) i);
                preparedStatement.setString(2, "Zhang San" + i);
                preparedStatement.setString(3, "张三" + i);
                preparedStatement.addBatch();
            }

            assertBatchInsertResult(preparedStatement.executeBatch(), Byte.MAX_VALUE);

            ResultSet rs = statement.executeQuery("select * from test");
            boolean hasResult = false;
            for (int i = 0; i < Byte.MAX_VALUE && rs.next(); i++) {
                hasResult = true;
                assertEquals(i, rs.getByte(1));
                assertEquals(1, rs.getByte(2));
                assertEquals("Zhang San" + i, rs.getString(3));
                assertEquals("张三" + i, rs.getString(4));
            }
            assertTrue(hasResult);
        });

    }

    @Test
    public void successfullyMultipleBatchInsert() throws Exception {
        withNewConnection(connection -> {
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

            assertBatchInsertResult(preparedStatement.executeBatch(), insertBatchSize);

            for (int i = 0; i < insertBatchSize; i++) {
                preparedStatement.setByte(1, (byte) i);
                preparedStatement.setString(2, "Zhang San" + i);
                preparedStatement.addBatch();
            }
            assertBatchInsertResult(preparedStatement.executeBatch(), insertBatchSize);
        });

    }

    @Test
    public void successfullyNullableDataType() throws Exception {
        withNewConnection(connection -> {
            Statement stmt = connection.createStatement();
            int insertBatchSize = 100;

            stmt.executeQuery("DROP TABLE IF EXISTS test");
            stmt.executeQuery(
                    "create table test(day Date, name Nullable(String), name2 Nullable(FixedString(10)) ) Engine=Memory");
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
            assertBatchInsertResult(pstmt.executeBatch(), insertBatchSize);

            ResultSet rs = stmt.executeQuery("select name, name2 from test order by name");
            int i = 0;
            while (rs.next()) {
                String name1 = rs.getString(1);
                String name2 = rs.getString(2);

                if (i * 2 >= insertBatchSize) {
                    assertNull(name1);
                } else {
                    assertEquals("String", name1);
                    assertTrue(name2.contains("String"));
                    assertEquals(10, name2.length());
                }
                i++;
            }

            rs = stmt.executeQuery(
                    "select countIf(isNull(name)), countIf(isNotNull(name)), countIf(isNotNull(name2))  from test;");
            assertTrue(rs.next());
            assertEquals(insertBatchSize / 2, rs.getInt(1));
            assertEquals(insertBatchSize / 2, rs.getInt(2));
            assertEquals(insertBatchSize / 2, rs.getInt(3));

            stmt.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

    @Test
    public void successfullyBatchInsertArray() throws Exception {
        withNewConnection(connection -> {
            System.setProperty("illegal-access", "allow");

            Statement statement = connection.createStatement();

            statement.execute("DROP TABLE IF EXISTS test");
            statement.execute(
                    "CREATE TABLE test(value0 Array(String), value1 Array(Float64), value2 Array(Array(Int32)), array3 Array(Nullable(Float64)))ENGINE=Log");
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO test VALUES(?, ?, [[1,2,3]], ?)");

            List<String> array0 = Arrays.asList("aa", "bb", "cc");
            List<Double> array1 = Arrays.asList(1.2, 2.2, 3.2);
            List<Double> array3 = Arrays.asList(1.2, 2.2, 3.2, null);

            for (int i = 0; i < Byte.MAX_VALUE; i++) {
                preparedStatement.setArray(1, connection.createArrayOf("text", array0.toArray()));
                preparedStatement.setArray(2, connection.createArrayOf("text", array1.toArray()));
                preparedStatement.setArray(3, connection.createArrayOf("text", array3.toArray()));

                preparedStatement.addBatch();
            }

            assertBatchInsertResult(preparedStatement.executeBatch(), Byte.MAX_VALUE);

            ResultSet rs = statement.executeQuery("select * from test");
            while (rs.next()) {
                assertArrayEquals(array0.toArray(), (Object[]) rs.getArray(1).getArray());
                assertArrayEquals(array1.toArray(), (Object[]) rs.getArray(2).getArray());
                assertArrayEquals(array3.toArray(), (Object[]) rs.getArray(4).getArray());
            }
        });

    }

    @Test
    public void successfullyBatchInsertDateTime() throws Exception {
        withNewConnection(connection -> {
            Statement statement = connection.createStatement();

            statement.execute("DROP TABLE IF EXISTS test");
            statement.execute("CREATE TABLE test(time DateTime)ENGINE=Log");
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO test VALUES(?)");

            // 2018-07-01 00:00:00 Asia/Shanghai
            long time = 1530374400;
            long insertTime = time;
            for (int i = 0; i < 24; i++) {
                preparedStatement.setTimestamp(1, new Timestamp(insertTime * 1000));
                preparedStatement.addBatch();
                insertTime += 3600;
            }

            assertBatchInsertResult(preparedStatement.executeBatch(), 24);

            long selectTime = time;
            ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY time ASC");
            while (rs.next()) {
                assertEquals(selectTime * 1000, rs.getTimestamp(1).getTime());
                selectTime += 3600;
            }
        });

    }
}
