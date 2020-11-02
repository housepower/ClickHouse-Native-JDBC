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

package examples;

import java.sql.*;

/**
 * BatchQuery
 */
public class BatchQuery {

    public static void main(String[] args) throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000")) {
            try (Statement stmt = connection.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("drop table if exists test_jdbc_example")) {
                    System.out.println(rs.next());
                }
                try (ResultSet rs = stmt.executeQuery("create table test_jdbc_example(day Date, name String, age UInt8) Engine=Log")) {
                    System.out.println(rs.next());
                }
                try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO test_jdbc_example VALUES(?, ?, ?)")) {
                    for (int i = 1; i <= 200; i++) {
                        pstmt.setDate(1, new Date(System.currentTimeMillis()));
                        if (i % 2 == 0)
                            pstmt.setString(2, "Zhang San" + i);
                        else
                            pstmt.setString(2, "Zhang San");
                        pstmt.setByte(3, (byte) ((i % 4) * 15));
                        System.out.println(pstmt);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }

                try (PreparedStatement pstmt = connection.prepareStatement("select count(*) from test_jdbc_example where age>? and age<=?")) {
                    pstmt.setByte(1, (byte) 10);
                    pstmt.setByte(2, (byte) 30);
                    printCount(pstmt);
                }

                try (PreparedStatement pstmt = connection.prepareStatement("select count(*) from test_jdbc_example where name=?")) {
                    pstmt.setString(1, "Zhang San");
                    printCount(pstmt);
                }
                try (ResultSet rs = stmt.executeQuery("drop table test_jdbc_example")) {
                    System.out.println(rs.next());
                }
            }
        }
    }

    public static void printCount(PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            System.out.println(pstmt);
            if (rs.next())
                System.out.println(rs.getInt(1));
        }
    }
}
