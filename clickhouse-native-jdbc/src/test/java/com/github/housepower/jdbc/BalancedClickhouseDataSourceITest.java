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

import com.github.housepower.settings.ClickHouseConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.Locale;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class BalancedClickhouseDataSourceITest extends AbstractITest {

    private static BalancedClickhouseDataSource singleDs;
    private static BalancedClickhouseDataSource dualDs;

    @BeforeEach
    public void reset() {
        singleDs = new BalancedClickhouseDataSource(String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s", CK_HOST, CK_PORT));
        dualDs = new BalancedClickhouseDataSource(String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s,%s:%s", CK_HOST, CK_PORT, CK_IP, CK_PORT));
    }

    @Test
    public void testSingleDatabaseConnection() throws Exception {
        Connection connection = singleDs.getConnection();
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
        Connection connection = dualDs.getConnection();
        connection.createStatement().execute("CREATE DATABASE IF NOT EXISTS test");
        connection = dualDs.getConnection();
        connection.createStatement().execute("DROP TABLE IF EXISTS test.insert_test");
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS test.insert_test (i Int32, s String) ENGINE = TinyLog");

        connection = dualDs.getConnection();

        PreparedStatement statement = connection.prepareStatement("INSERT INTO test.insert_test (s, i) VALUES (?, ?)");
        statement.setString(1, "asd");
        statement.setInt(2, 42);
        statement.execute();

        ResultSet rs = connection.createStatement().executeQuery("SELECT * from test.insert_test");
        rs.next();

        assertEquals("asd", rs.getString("s"));
        assertEquals(42, rs.getInt("i"));

        connection = dualDs.getConnection();

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
        singleDs.actualize();
        try (Connection connection = singleDs.getConnection()) {
            assertTrue(connection.isValid(1000));
        }
    }


    @Test
    public void testDisableConnection() {
        BalancedClickhouseDataSource badDatasource = new BalancedClickhouseDataSource(
                "jdbc:clickhouse://not.existed.url:" + CK_PORT, new Properties());
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
                String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s,%s:%s", "not.existed.url", CK_PORT, CK_IP, CK_PORT), new Properties());

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
        properties.setProperty("query_timeout", "6789");
        properties.setProperty("password", "888888");

        // without connection parameters
        BalancedClickhouseDataSource dataSource = new BalancedClickhouseDataSource(
                String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s,%s:%s/click", CK_HOST, CK_PORT, CK_IP, CK_PORT), properties);
        ClickHouseConfig cfg = dataSource.getCfg();
        assertEquals(Duration.ofSeconds(6789), cfg.queryTimeout());
        assertEquals("888888", cfg.password());
        assertEquals("click", cfg.database());
        assertEquals(2, dataSource.getAllClickhouseUrls().size());
        assertEquals(String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s/click", CK_HOST, CK_PORT),
                dataSource.getAllClickhouseUrls().get(0));
        assertEquals(String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s/click", CK_IP, CK_PORT),
                dataSource.getAllClickhouseUrls().get(1));

        // with connection parameters
        dataSource = new BalancedClickhouseDataSource(
                String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s,%s:%s/click?query_timeout=12345&user=readonly", CK_HOST, CK_PORT, CK_IP, CK_PORT), properties);
        cfg = dataSource.getCfg();
        assertEquals(Duration.ofSeconds(6789), cfg.queryTimeout());
        assertEquals("readonly", cfg.user());
        assertEquals("888888", cfg.password());
        assertEquals("click", cfg.database());
        assertEquals(2, dataSource.getAllClickhouseUrls().size());
        assertEquals(String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s/click?query_timeout=12345&user=readonly", CK_HOST, CK_PORT),
                dataSource.getAllClickhouseUrls().get(0));
        assertEquals(String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s/click?query_timeout=12345&user=readonly", CK_IP, CK_PORT),
                dataSource.getAllClickhouseUrls().get(1));
    }
}
