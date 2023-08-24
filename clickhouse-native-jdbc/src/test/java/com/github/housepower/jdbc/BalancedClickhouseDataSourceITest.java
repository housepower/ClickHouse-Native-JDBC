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
        dualDs = new BalancedClickhouseDataSource(String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s,%s:%s", CK_HOST, CK_PORT, CK_HOST, CK_PORT));
    }

    @Test
    public void testSingleDatabaseConnection() throws Exception {
        withNewConnection(singleDs, connection -> {
            withStatement(connection, stmt -> stmt.execute("CREATE DATABASE IF NOT EXISTS test"));
            withStatement(connection, stmt -> stmt.execute("DROP TABLE IF EXISTS test.insert_test"));
            withStatement(connection, stmt -> stmt.execute("CREATE TABLE IF NOT EXISTS test.insert_test (i Int32, s String) ENGINE = TinyLog"));

            withPreparedStatement(connection, "INSERT INTO test.insert_test (s, i) VALUES (?, ?)", pstmt -> {
                pstmt.setString(1, "asd");
                pstmt.setInt(2, 42);
                pstmt.execute();

                ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM test.insert_test");
                rs.next();

                assertEquals("asd", rs.getString("s"));
                assertEquals(42, rs.getInt("i"));
            });
        });
    }

    @Test
    public void testDualDatabaseConnection() throws Exception {
        withNewConnection(dualDs, connection ->
                withStatement(connection, stmt -> stmt.execute("CREATE DATABASE IF NOT EXISTS test"))
        );

        withNewConnection(dualDs, connection -> {
            withStatement(connection, stmt -> stmt.execute("DROP TABLE IF EXISTS test.insert_test"));
            withStatement(connection, stmt -> stmt.execute("CREATE TABLE IF NOT EXISTS test.insert_test (i Int32, s String) ENGINE = TinyLog"));
        });

        withNewConnection(dualDs, connection -> {
            withPreparedStatement(connection, "INSERT INTO test.insert_test (s, i) VALUES (?, ?)", pstmt -> {
                pstmt.setString(1, "asd");
                pstmt.setInt(2, 42);
                pstmt.execute();
            });
            withStatement(connection, stmt -> {
                ResultSet rs = stmt.executeQuery("SELECT * FROM test.insert_test");
                rs.next();
                assertEquals("asd", rs.getString("s"));
                assertEquals(42, rs.getInt("i"));
            });
        });

        withNewConnection(dualDs, connection -> {
            withPreparedStatement(connection, "INSERT INTO test.insert_test (s, i) VALUES (?, ?)", pstmt -> {
                pstmt.setString(1, "asd");
                pstmt.setInt(2, 42);
                pstmt.execute();
            });

            withStatement(connection, stmt -> {
                ResultSet rs = stmt.executeQuery("SELECT * from test.insert_test");
                rs.next();

                assertEquals("asd", rs.getString("s"));
                assertEquals(42, rs.getInt("i"));
            });
        });
    }

    @Test
    public void testCorrectActualizationDatabaseConnection() throws Exception {
        singleDs.actualize();
        withNewConnection(singleDs, connection -> assertTrue(connection.isValid(1000)));
    }

    @Test
    public void testDisableConnection() {
        BalancedClickhouseDataSource badDatasource = new BalancedClickhouseDataSource(
                "jdbc:clickhouse://not.existed.url:" + CK_PORT, new Properties());
        badDatasource.actualize();
        try (Connection ignored = badDatasource.getConnection()) {
            fail();
        } catch (Exception e) {
            // There is no enabled connections
        }
    }

    @Test
    public void testWorkWithEnabledUrl() throws Exception {
        BalancedClickhouseDataSource halfDatasource = new BalancedClickhouseDataSource(
                String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s,%s:%s", "not.existed.url", CK_PORT, CK_HOST, CK_PORT), new Properties());

        halfDatasource.actualize();

        withNewConnection(halfDatasource, connection ->
                withStatement(connection, stmt -> stmt.execute("CREATE DATABASE IF NOT EXISTS test"))
        );

        withNewConnection(halfDatasource, connection -> {
            withStatement(connection, stmt -> stmt.execute("DROP TABLE IF EXISTS test.insert_test"));
            withStatement(connection, stmt -> stmt.execute("CREATE TABLE IF NOT EXISTS test.insert_test (i Int32, s String) ENGINE = TinyLog"));
        });

        withNewConnection(halfDatasource, connection -> {
            withPreparedStatement(connection, "INSERT INTO test.insert_test (s, i) VALUES (?, ?)", pstmt -> {
                pstmt.setString(1, "asd");
                pstmt.setInt(2, 42);
                pstmt.execute();
            });

            withStatement(connection, stmt -> {
                ResultSet rs = stmt.getConnection().createStatement().executeQuery("SELECT * FROM test.insert_test");
                rs.next();
                assertEquals("asd", rs.getString("s"));
                assertEquals(42, rs.getInt("i"));
            });
        });

        withNewConnection(halfDatasource, connection -> {
            withPreparedStatement(connection, "INSERT INTO test.insert_test (s, i) VALUES (?, ?)", pstmt -> {
                pstmt.setString(1, "asd");
                pstmt.setInt(2, 42);
                pstmt.execute();
            });

            withStatement(connection, stmt -> {
                ResultSet rs = stmt.executeQuery("SELECT * from test.insert_test");
                rs.next();
                assertEquals("asd", rs.getString("s"));
                assertEquals(42, rs.getInt("i"));
            });
        });
    }

    @Test
    public void testConstructWithProperties() {
        final Properties properties = new Properties();
        properties.setProperty("query_timeout", "6789");
        properties.setProperty("password", "888888");

        // without connection parameters
        BalancedClickhouseDataSource dataSource = new BalancedClickhouseDataSource(
                String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s,%s:%s/click", CK_HOST, CK_PORT, CK_HOST, CK_PORT), properties);
        ClickHouseConfig cfg = dataSource.getCfg();
        assertEquals(Duration.ofSeconds(6789), cfg.queryTimeout());
        assertEquals("888888", cfg.password());
        assertEquals("click", cfg.database());
        assertEquals(2, dataSource.getAllClickhouseUrls().size());
        assertEquals(String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s/click", CK_HOST, CK_PORT),
                dataSource.getAllClickhouseUrls().get(0));
        assertEquals(String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s/click", CK_HOST, CK_PORT),
                dataSource.getAllClickhouseUrls().get(1));

        // with connection parameters
        dataSource = new BalancedClickhouseDataSource(
                String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s,%s:%s/click?query_timeout=12345&user=readonly", CK_HOST, CK_PORT, CK_HOST, CK_PORT), properties);
        cfg = dataSource.getCfg();
        assertEquals(Duration.ofSeconds(6789), cfg.queryTimeout());
        assertEquals("readonly", cfg.user());
        assertEquals("888888", cfg.password());
        assertEquals("click", cfg.database());
        assertEquals(2, dataSource.getAllClickhouseUrls().size());
        assertEquals(String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s/click?query_timeout=12345&user=readonly", CK_HOST, CK_PORT),
                dataSource.getAllClickhouseUrls().get(0));
        assertEquals(String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s/click?query_timeout=12345&user=readonly", CK_HOST, CK_PORT),
                dataSource.getAllClickhouseUrls().get(1));
    }
}
