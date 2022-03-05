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

import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ClickHouseContainer;
import org.testcontainers.junit.jupiter.Container;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FailoverClickhouseConnectionITest extends AbstractITest {
    private static final Logger LOG = LoggerFactory.getLogger(FailoverClickhouseConnectionITest.class);

    protected static String HA_HOST;
    protected static int HA_PORT;

    @Container
    public static ClickHouseContainer containerHA = (ClickHouseContainer) new ClickHouseContainer(CLICKHOUSE_IMAGE)
            .withEnv("CLICKHOUSE_USER", CLICKHOUSE_USER)
            .withEnv("CLICKHOUSE_PASSWORD", CLICKHOUSE_PASSWORD)
            .withEnv("CLICKHOUSE_DB", CLICKHOUSE_DB);


    @BeforeEach
    public void reset() throws SQLException {
        resetDriverManager();
        container.start();
        containerHA.start();

        CK_PORT = container.getMappedPort(ClickHouseContainer.NATIVE_PORT);
        HA_HOST = containerHA.getHost();
        HA_PORT = containerHA.getMappedPort(ClickHouseContainer.NATIVE_PORT);
        LOG.info("Port1 {}, Port2 {}", CK_PORT, HA_PORT);
    }

    @Test
    public void testClickhouseDownBeforeConnect() throws Exception {
        String haHost = String.format(Locale.ROOT, "%s:%s,%s:%s", CK_HOST, CK_PORT, HA_HOST, HA_PORT);

        container.stop();
        try (Connection connection = DriverManager
                .getConnection(String.format(Locale.ROOT, "jdbc:clickhouse://%s/default", haHost))
        ) {
            withStatement(connection, stmt -> {
                ResultSet rs = stmt.executeQuery("select count() from system.tables");

                if (rs.next()) {
                    assertTrue(rs.getLong(1) > 0);
                }
            });
        }
    }

    @Test
    public void testClickhouseDownBeforeStatement() throws Exception {
        String haHost = String.format(Locale.ROOT, "%s:%s,%s:%s", CK_HOST, CK_PORT, HA_HOST, HA_PORT);

        try (Connection connection = DriverManager
                .getConnection(String.format(Locale.ROOT, "jdbc:clickhouse://%s/default", haHost))
        ) {
            container.stop();
            withStatement(connection, stmt -> {
                ResultSet rs = stmt.executeQuery("select count() from system.tables");

                if (rs.next()) {
                    assertTrue(rs.getLong(1) > 0);
                }
            });
        }
    }

    @Test
    public void testClickhouseDownBeforePrepareStatement() throws Exception {
        String haHost = String.format(Locale.ROOT, "%s:%s,%s:%s", CK_HOST, CK_PORT, HA_HOST, HA_PORT);

        try (Connection connection = DriverManager
                .getConnection(String.format(Locale.ROOT, "jdbc:clickhouse://%s/default", haHost))
        ) {
            container.stop();
            withPreparedStatement(connection, "select count() from system.tables", stmt -> {
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    assertTrue(rs.getLong(1) > 0);
                }
            });
        }
    }

    @Test
    public void testClickhouseDownBeforeExecute() throws Exception {
        String haHost = String.format(Locale.ROOT, "%s:%s,%s:%s", CK_HOST, CK_PORT, HA_HOST, HA_PORT);

        try (Connection connection = DriverManager
                .getConnection(String.format(Locale.ROOT, "jdbc:clickhouse://%s/default", haHost))
        ) {
            withStatement(connection, stmt -> {
                container.stop();
                ResultSet rs = stmt.executeQuery("select count() from system.tables");

                if (rs.next()) {
                    assertTrue(rs.getLong(1) > 0);
                }
            });
        }
    }

    @Test
    public void testClickhouseDownBeforeAndAfterConnect() {
        String haHost = String.format(Locale.ROOT, "%s:%s,%s:%s", CK_HOST, CK_PORT, HA_HOST, HA_PORT);

        Exception ex = null;
        container.stop();
        try (Connection connection = DriverManager
                .getConnection(String.format(Locale.ROOT, "jdbc:clickhouse://%s/default?query_id=xxx", haHost))
        ) {
            containerHA.stop();
            withStatement(connection, stmt -> {
                ResultSet rs = stmt.executeQuery("select count() from system.tables");

                if (rs.next()) {
                    assertTrue(rs.getLong(1) > 0);
                }
            });
        } catch (Exception e) {
            ex = e;
        }

        assertNotNull(ex);
    }

    @Test
    public void testClickhouseAllDownBeforeConnect() throws Exception {
        String haHost = String.format(Locale.ROOT, "%s:%s,%s", CK_HOST, CK_PORT, HA_HOST);

        Exception ex = null;
        container.stop();
        containerHA.stop();
        try (Connection connection = DriverManager
                .getConnection(String.format(Locale.ROOT, "jdbc:clickhouse://%s/default", haHost))
        ) {
            withStatement(connection, stmt -> {

                ResultSet rs = stmt.executeQuery("select count() from system.tables");

                if (rs.next()) {
                    assertTrue(rs.getLong(1) > 0);
                }
            });
        } catch (Exception e) {
            ex = e;
        }

        assertNotNull(ex);
    }
}
