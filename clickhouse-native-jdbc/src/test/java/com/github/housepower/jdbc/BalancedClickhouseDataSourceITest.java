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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class BalancedClickhouseDataSourceITest {

    private static BalancedClickhouseDataSource dataSource;
    private static BalancedClickhouseDataSource doubleDataSource;

    @BeforeEach
    public void reset() {
        dataSource = new BalancedClickhouseDataSource("jdbc:clickhouse://127.0.0.1:9000");
        doubleDataSource = new BalancedClickhouseDataSource("jdbc:clickhouse://127.0.0.1:9000,127.0.0.1:9000");
    }

    @Test
    public void testSingleDatabaseConnection() throws Exception {
        Connection connection = dataSource.getConnection();
        connection.createStatement().execute("CREATE DATABASE IF NOT EXISTS test");
        connection.createStatement().execute("DROP TABLE IF EXISTS test.insert_test");
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS test.insert_test (i Int32, s String) ENGINE = TinyLog");
        PreparedStatement statement = connection.prepareStatement("INSERT INTO test.insert_test (s, i) VALUES (?, ?)");
        statement.setString(1, "asd");
        statement.setInt(2, 42);
        statement.execute();

        ResultSet rs = connection.createStatement().executeQuery("SELECT * from test.insert_test");
        rs.next();

        assertEquals("asd", rs.getString("s"));
        assertEquals(42, rs.getInt("i"));
    }

    @Test
    public void testDoubleDatabaseConnection() throws Exception {
        Connection connection = doubleDataSource.getConnection();
        connection.createStatement().execute("CREATE DATABASE IF NOT EXISTS test");
        connection = doubleDataSource.getConnection();
        connection.createStatement().execute("DROP TABLE IF EXISTS test.insert_test");
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS test.insert_test (i Int32, s String) ENGINE = TinyLog");

        connection = doubleDataSource.getConnection();

        PreparedStatement statement = connection.prepareStatement("INSERT INTO test.insert_test (s, i) VALUES (?, ?)");
        statement.setString(1, "asd");
        statement.setInt(2, 42);
        statement.execute();

        ResultSet rs = connection.createStatement().executeQuery("SELECT * from test.insert_test");
        rs.next();

        assertEquals("asd", rs.getString("s"));
        assertEquals(42, rs.getInt("i"));

        connection = doubleDataSource.getConnection();

        statement = connection.prepareStatement("INSERT INTO test.insert_test (s, i) VALUES (?, ?)");
        statement.setString(1, "asd");
        statement.setInt(2, 42);
        statement.execute();

        rs = connection.createStatement().executeQuery("SELECT * from test.insert_test");
        rs.next();

        assertEquals("asd", rs.getString("s"));
        assertEquals(42, rs.getInt("i"));

    }


    @Test
    public void testCorrectActualizationDatabaseConnection() throws Exception {
        dataSource.actualize();
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(connection.isValid(1000));
        }
    }


    @Test
    public void testDisableConnection() {
        BalancedClickhouseDataSource badDatasource = new BalancedClickhouseDataSource(
                "jdbc:clickhouse://not.existed.url:9000", new Properties());
        badDatasource.actualize();
        try {
            Connection connection = badDatasource.getConnection();
            fail();
        } catch (Exception e) {
            // There is no enabled connections
        }
    }


    @Test
    public void testWorkWithEnabledUrl() throws Exception {
        BalancedClickhouseDataSource halfDatasource = new BalancedClickhouseDataSource(
                "jdbc:clickhouse://not.existed.url:9000,127.0.0.1:9000", new Properties());

        halfDatasource.actualize();
        Connection connection = halfDatasource.getConnection();

        connection.createStatement().execute("CREATE DATABASE IF NOT EXISTS test");
        connection = halfDatasource.getConnection();
        connection.createStatement().execute("DROP TABLE IF EXISTS test.insert_test");
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS test.insert_test (i Int32, s String) ENGINE = TinyLog"
        );

        connection = halfDatasource.getConnection();

        PreparedStatement statement = connection.prepareStatement("INSERT INTO test.insert_test (s, i) VALUES (?, ?)");
        statement.setString(1, "asd");
        statement.setInt(2, 42);
        statement.execute();

        ResultSet rs = connection.createStatement().executeQuery("SELECT * from test.insert_test");
        rs.next();

        assertEquals("asd", rs.getString("s"));
        assertEquals(42, rs.getInt("i"));

        connection = halfDatasource.getConnection();

        statement = connection.prepareStatement("INSERT INTO test.insert_test (s, i) VALUES (?, ?)");
        statement.setString(1, "asd");
        statement.setInt(2, 42);
        statement.execute();

        rs = connection.createStatement().executeQuery("SELECT * from test.insert_test");
        rs.next();

        assertEquals("asd", rs.getString("s"));
        assertEquals(42, rs.getInt("i"));
    }

    @Test
    public void testConstructWithProperties() {
        final Properties properties = new Properties();
        properties.put("query_timeout", 6789);
        properties.put("password", "888888");

        // without connection parameters
        BalancedClickhouseDataSource dataSource = new BalancedClickhouseDataSource(
                "jdbc:clickhouse://localhost:9000,127.0.0.1:9000/click", properties);
        ClickHouseConfig cfg = dataSource.getCfg();
        assertEquals(Duration.ofSeconds(6789), cfg.queryTimeout());
        assertEquals("888888", cfg.password());
        assertEquals("click", cfg.database());
        assertEquals(2, dataSource.getAllClickhouseUrls().size());
        assertEquals(dataSource.getAllClickhouseUrls().get(0), "jdbc:clickhouse://localhost:9000/click");
        assertEquals(dataSource.getAllClickhouseUrls().get(1), "jdbc:clickhouse://127.0.0.1:9000/click");

        // with connection parameters
        dataSource = new BalancedClickhouseDataSource(
                "jdbc:clickhouse://localhost:9000,127.0.0.1:9000/click?query_timeout=12345&user=readonly", properties);
        cfg = dataSource.getCfg();
        assertEquals(Duration.ofSeconds(6789), cfg.queryTimeout());
        assertEquals("readonly", cfg.user());
        assertEquals("888888", cfg.password());
        assertEquals("click", cfg.database());
        assertEquals(2, dataSource.getAllClickhouseUrls().size());
        assertEquals("jdbc:clickhouse://localhost:9000/click?query_timeout=12345&user=readonly",
                dataSource.getAllClickhouseUrls().get(0));
        assertEquals("jdbc:clickhouse://127.0.0.1:9000/click?query_timeout=12345&user=readonly",
                dataSource.getAllClickhouseUrls().get(1));
    }
}
