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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * SimpleQuery
 */
public class SimpleQuery {

    public static void main(String[] args) throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000")) {
            try (Statement stmt = connection.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(
                        "SELECT (number % 3 + 1) as n, sum(number) FROM numbers(10000000) GROUP BY n")) {
                    while (rs.next()) {
                        System.out.println(rs.getInt(1) + "\t" + rs.getLong(2));
                    }
                }
            }
        }
    }
}
