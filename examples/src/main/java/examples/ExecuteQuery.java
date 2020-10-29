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
import java.sql.Statement;

/**
 * ExecuteQuery
 */
public class ExecuteQuery {

    public static void main(String[] args) throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000")) {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeQuery("drop table if exists test_jdbc_example");
                stmt.executeQuery("create table test_jdbc_example(" +
                        "day default toDate( toDateTime(timestamp) ), " +
                        "timestamp UInt32, " +
                        "name String, " +
                        "impressions UInt32" +
                        ") Engine=MergeTree(day, (timestamp, name), 8192)");
                stmt.executeQuery("alter table test_jdbc_example add column costs Float32");
                stmt.executeQuery("drop table test_jdbc_example");
            }
        }
    }
}
