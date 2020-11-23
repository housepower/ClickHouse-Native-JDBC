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

import com.github.housepower.jdbc.settings.ClickHouseConfig;
import com.github.housepower.jdbc.settings.SettingKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConnectionParamITest extends AbstractITest {

    @BeforeEach
    public void init() throws SQLException {
        resetDriverManager();
    }

    @Test
    public void successfullyMaxRowsToRead() {
        assertThrows(SQLException.class, () -> {
            Connection connection = DriverManager
                    .getConnection("jdbc:clickhouse://127.0.0.1?max_rows_to_read=1&connect_timeout=10");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT arrayJoin([1,2,3,4]) from numbers(100)");
            int rowsRead = 0;
            while (rs.next()) {
                ++rowsRead;
            }
            assertEquals(1, rowsRead); // not reached
        });
    }

    @Test
    public void successfullyMaxResultRows() throws Exception {
        Connection connection = DriverManager
                .getConnection("jdbc:clickhouse://127.0.0.1?max_result_rows=1&connect_timeout=10");
        Statement statement = connection.createStatement();
        statement.setMaxRows(400);
        ResultSet rs = statement.executeQuery("SELECT arrayJoin([1,2,3,4]) from numbers(100)");
        int rowsRead = 0;
        while (rs.next()) {
            ++rowsRead;
        }
        assertEquals(400, rowsRead);
    }

    @Test
    public void successfullyUrlParser() {
        String url = "jdbc:clickhouse://127.0.0.1/system?min_insert_block_size_rows=1000&connect_timeout=50";
        ClickHouseConfig config = ClickHouseConfig.Builder.builder().withJdbcUrl(url).build();
        assertEquals("system", config.database());
        assertEquals(1000L, config.settings().get(SettingKey.min_insert_block_size_rows));

        assertEquals(Duration.ofSeconds(50), config.connectTimeout());
    }

    @Test
    public void successfullyHostNameOnly() {
        String url = "jdbc:clickhouse://my_clickhouse_sever_host_name/system?min_insert_block_size_rows=1000&connect_timeout=50";
        ClickHouseConfig config = ClickHouseConfig.Builder.builder().withJdbcUrl(url).build();
        assertEquals("my_clickhouse_sever_host_name", config.host());
        assertEquals(9000, config.port());
        assertEquals("system", config.database());
        assertEquals(1000L, config.settings().get(SettingKey.min_insert_block_size_rows));
        assertEquals(Duration.ofSeconds(50), config.connectTimeout());
    }

    @Test
    public void successfullyHostNameWithDefaultPort() {
        String url = "jdbc:clickhouse://my_clickhouse_sever_host_name:9000/system?min_insert_block_size_rows=1000&connect_timeout=50";
        ClickHouseConfig config = ClickHouseConfig.Builder.builder().withJdbcUrl(url).build();
        assertEquals("my_clickhouse_sever_host_name", config.host());
        assertEquals(9000, config.port());
        assertEquals("system", config.database());
        assertEquals(1000L, config.settings().get(SettingKey.min_insert_block_size_rows));
        assertEquals(Duration.ofSeconds(50), config.connectTimeout());
    }

    @Test
    public void successfullyHostNameWithCustomPort() {
        String url = "jdbc:clickhouse://my_clickhouse_sever_host_name:1940/system?min_insert_block_size_rows=1000&connect_timeout=50";
        ClickHouseConfig config = ClickHouseConfig.Builder.builder().withJdbcUrl(url).build();
        assertEquals("my_clickhouse_sever_host_name", config.host());
        assertEquals(1940, config.port());
        assertEquals("system", config.database());
        assertEquals(1000L, config.settings().get(SettingKey.min_insert_block_size_rows));
        assertEquals(Duration.ofSeconds(50), config.connectTimeout());
    }

}
