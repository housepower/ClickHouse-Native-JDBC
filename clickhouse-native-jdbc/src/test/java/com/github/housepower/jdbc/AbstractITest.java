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

import com.github.housepower.misc.StrUtil;
import com.github.housepower.misc.SystemUtil;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.clickhouse.ClickHouseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.*;
import java.time.ZoneId;
import java.util.Enumeration;

@Testcontainers
public abstract class AbstractITest implements Serializable {

    protected static final ZoneId CLIENT_TZ = ZoneId.systemDefault();
    protected static final ZoneId SERVER_TZ = ZoneId.of("UTC");
    protected static final String DRIVER_CLASS_NAME = "com.github.housepower.jdbc.ClickHouseDriver";

    public static final String CLICKHOUSE_IMAGE = System.getProperty("CLICKHOUSE_IMAGE", "clickhouse/clickhouse-server:21.9");

    protected static final String CLICKHOUSE_USER = SystemUtil.loadProp("CLICKHOUSE_USER", "default");
    protected static final String CLICKHOUSE_PASSWORD = SystemUtil.loadProp("CLICKHOUSE_PASSWORD", "");
    protected static final String CLICKHOUSE_DB = SystemUtil.loadProp("CLICKHOUSE_DB", "");

    protected static final int CLICKHOUSE_HTTP_PORT = 8123;
    protected static final int CLICKHOUSE_HTTPS_PORT = 8443;
    protected static final int CLICKHOUSE_NATIVE_PORT = 9000;
    protected static final int CLICKHOUSE_NATIVE_SECURE_PORT = 9440;

    @Container
    public static ClickHouseContainer container = new ClickHouseContainer(CLICKHOUSE_IMAGE)
            .withEnv("CLICKHOUSE_USER", CLICKHOUSE_USER)
            .withEnv("CLICKHOUSE_PASSWORD", CLICKHOUSE_PASSWORD)
            .withEnv("CLICKHOUSE_DB", CLICKHOUSE_DB)
            .withExposedPorts(CLICKHOUSE_HTTP_PORT,
                    CLICKHOUSE_HTTPS_PORT,
                    CLICKHOUSE_NATIVE_PORT,
                    CLICKHOUSE_NATIVE_SECURE_PORT)
            .withCopyFileToContainer(MountableFile.forClasspathResource("clickhouse/config/config.xml"),
                    "/etc/clickhouse-server/config.xml")
            .withCopyFileToContainer(MountableFile.forClasspathResource("clickhouse/config/users.xml"),
                    "/etc/clickhouse-server/users.xml")
            .withCopyFileToContainer(MountableFile.forClasspathResource("clickhouse/server.key"),
                    "/etc/clickhouse-server/server.key")
            .withCopyFileToContainer(MountableFile.forClasspathResource("clickhouse/server.crt"),
                    "/etc/clickhouse-server/server.crt");

    protected static String CK_HOST;
    protected static int CK_PORT;

    @BeforeAll
    public static void extractContainerInfo() {
        CK_HOST = container.getHost();
        CK_PORT = container.getMappedPort(CLICKHOUSE_NATIVE_PORT);
    }

    /**
     * just for compatible with scala
     */
    protected String getJdbcUrl() {
        return getJdbcUrl("");
    }

    protected String getJdbcUrl(Object... params) {
        StringBuilder settingsStringBuilder = new StringBuilder();
        for (int i = 0; i + 1 < params.length; i++) {
            settingsStringBuilder.append(i == 0 ? "?" : "&");
            settingsStringBuilder.append(params[i]).append("=").append(params[i + 1]);
        }

        StringBuilder mainStringBuilder = new StringBuilder();
        int port = 0;
        if (settingsStringBuilder.indexOf("ssl=true") == -1) {
            port = container.getMappedPort(CLICKHOUSE_NATIVE_PORT);
        } else {
            port = container.getMappedPort(CLICKHOUSE_NATIVE_SECURE_PORT);
        }

        mainStringBuilder.append("jdbc:clickhouse://").append(container.getHost()).append(":").append(port);
        if (StrUtil.isNotEmpty(CLICKHOUSE_DB)) {
            mainStringBuilder.append("/").append(container.getDatabaseName());
        }

        // Add settings
        mainStringBuilder.append(settingsStringBuilder);

        // Add user
        mainStringBuilder.append(params.length < 2 ? "?" : "&");
        mainStringBuilder.append("user=").append(container.getUsername());

        // Add password
        // ignore blank password
        if (!StrUtil.isBlank(CLICKHOUSE_PASSWORD)) {
            mainStringBuilder.append("&password=").append(container.getPassword());
        }

        return mainStringBuilder.toString();
    }

    // this method should be synchronized
    synchronized protected void resetDriverManager() throws SQLException {
        // remove all registered jdbc drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            DriverManager.deregisterDriver(drivers.nextElement());
        }
        DriverManager.registerDriver(new ClickHouseDriver());
    }

    protected void withNewConnection(WithConnection withConnection, Object... args) throws Exception {
        resetDriverManager();

        String connectionStr = getJdbcUrl(args);
        try (Connection connection = DriverManager.getConnection(connectionStr)) {
            withConnection.apply(connection);
        }
    }

    protected void withNewConnection(DataSource ds, WithConnection withConnection) throws Exception {
        try (Connection connection = ds.getConnection()) {
            withConnection.apply(connection);
        }
    }

    protected void withStatement(Connection connection, WithStatement withStatement) throws Exception {
        try (Statement stmt = connection.createStatement()) {
            withStatement.apply(stmt);
        }
    }

    protected void withStatement(WithStatement withStatement, Object... args) throws Exception {
        withNewConnection(connection -> withStatement(connection, withStatement), args);
    }

    protected void withPreparedStatement(Connection connection,
                                         String sql,
                                         WithPreparedStatement withPreparedStatement) throws Exception {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            withPreparedStatement.apply(pstmt);
        }
    }

    protected void withPreparedStatement(String sql,
                                         WithPreparedStatement withPreparedStatement,
                                         Object... args) throws Exception {
        withNewConnection(connection -> withPreparedStatement(connection, sql, withPreparedStatement), args);
    }

    @FunctionalInterface
    public interface WithConnection {
        void apply(Connection connection) throws Exception;
    }

    @FunctionalInterface
    public interface WithStatement {
        void apply(Statement stmt) throws Exception;
    }

    @FunctionalInterface
    public interface WithPreparedStatement {
        void apply(PreparedStatement pstmt) throws Exception;
    }
}
