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
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class QueryScanStatTest extends AbstractITest {

    @BeforeEach
    public void init() throws SQLException {
        resetDriverManager();
    }

    @Test
    public void readRowsAndReadBytes() throws Exception {
        String queryId = UUID.randomUUID().toString();
        double random = Math.random();
        long lines = (long) (random * 100000000);

        try (Connection connection = DriverManager
                .getConnection(String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s?query_id=%s", CK_HOST, CK_PORT,
                        queryId))
        ) {
            withStatement(connection, stmt -> {
                stmt.executeQuery("DROP TABLE IF EXISTS test_scan_stat1");
                stmt.executeQuery("CREATE TABLE test_scan_stat1 "
                        + "(c1 UInt32) "
                        + "ENGINE = MergeTree() "
                        + "PARTITION BY tuple()"
                        + "ORDER BY tuple() ");
                stmt.executeQuery("INSERT INTO test_scan_stat1 SELECT number FROM system.numbers LIMIT " + lines);

                long readRows;
                long readBytes;

                try (ResultSet rs = stmt.executeQuery("SELECT c1 FROM test_scan_stat1 LIMIT 100")) {
                    while (rs.next()) {
                        // ignore result
                    }

                    readRows = ((ClickHouseResultSet) rs).getReadRows();
                    readBytes = ((ClickHouseResultSet) rs).getReadBytes();
                }

                Thread.sleep(12 * 1000); // wait insert into ch log table

                try (ResultSet rs = stmt.executeQuery(String.format(Locale.ROOT,
                        "SELECT read_rows,read_bytes FROM system.query_log WHERE query_id='%s'"
                                + " and type='QueryFinish' and startsWith(query, 'SELECT') ORDER BY query_start_time asc", queryId))) {
                    if (rs.next()) {
                        assertEquals(rs.getLong("read_rows"), readRows);
                        assertEquals(rs.getLong("read_bytes"), readBytes);
                    }
                }
            });
        }
    }

    @Test
    public void readRows() throws Exception {
        double random = Math.random();
        long lines = (long) (random * 100000000);


        withStatement( stmt -> {
            stmt.executeQuery("DROP TABLE IF EXISTS test_scan_stat2");
            stmt.executeQuery("CREATE TABLE test_scan_stat2 "
                    + "(c1 UInt32) "
                    + "ENGINE = MergeTree() "
                    + "PARTITION BY tuple()"
                    + "ORDER BY tuple() ");
            stmt.executeQuery("INSERT INTO test_scan_stat2 SELECT number FROM system.numbers LIMIT " + lines);

            try (ResultSet rs = stmt.executeQuery("SELECT count(distinct c1) FROM test_scan_stat2")) {
                while (rs.next()) {
                    // ignore result
                }

                assertEquals(((ClickHouseResultSet) rs).getReadRows(), lines);
            }
        });
    }
}
