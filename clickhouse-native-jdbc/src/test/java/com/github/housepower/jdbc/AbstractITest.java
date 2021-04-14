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
import org.testcontainers.containers.ClickHouseContainer;
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

    // ClickHouse support gRPC from v21.1.2.15-stable 2021-01-18
    // link: https://github.com/ClickHouse/ClickHouse/blob/master/CHANGELOG.md#clickhouse-release-v211215-stable-2021-01-18
    public static final String CLICKHOUSE_IMAGE = SystemUtil.loadProp("CLICKHOUSE_IMAGE", "yandex/clickhouse-server:21.3");

    protected static final String CLICKHOUSE_USER = SystemUtil.loadProp("CLICKHOUSE_USER", "default");
    protected static final String CLICKHOUSE_PASSWORD = SystemUtil.loadProp("CLICKHOUSE_PASSWORD", "");
    protected static final String CLICKHOUSE_DB = SystemUtil.loadProp("CLICKHOUSE_DB", "");

    protected static final int CLICKHOUSE_GRPC_PORT = 9100;

    @Container
    public static ClickHouseContainer container = (ClickHouseContainer) new ClickHouseContainer(CLICKHOUSE_IMAGE)
            .withEnv("CLICKHOUSE_USER", CLICKHOUSE_USER)
            .withEnv("CLICKHOUSE_PASSWORD", CLICKHOUSE_PASSWORD)
            .withEnv("CLICKHOUSE_DB", CLICKHOUSE_DB)
            .withExposedPorts(CLICKHOUSE_GRPC_PORT)
            .withCopyFileToContainer(MountableFile.forClasspathResource("grpc_config.xml"), "/etc/clickhouse-server/config.d/grpc_config.xml");


    protected static String CK_HOST;
    protected static String CK_IP;
    protected static int CK_PORT;
    protected static int CK_GRPC_PORT;

    @BeforeAll
    public static void extractContainerInfo() {
        CK_HOST = container.getHost();
        CK_IP = container.getContainerIpAddress();
        CK_PORT = container.getMappedPort(ClickHouseContainer.NATIVE_PORT);
        CK_GRPC_PORT = container.getMappedPort(CLICKHOUSE_GRPC_PORT);
    }

    /**
     * just for compatible with scala
     */
    protected String getJdbcUrl() {
        return getJdbcUrl("");
    }

    protected String getJdbcUrl(Object... params) {
        StringBuilder sb = new StringBuilder();
        int port = container.getMappedPort(ClickHouseContainer.NATIVE_PORT);
        sb.append("jdbc:clickhouse://").append(container.getHost()).append(":").append(port);
        if (StrUtil.isNotEmpty(CLICKHOUSE_DB)) {
            sb.append("/").append(container.getDatabaseName());
        }
        for (int i = 0; i + 1 < params.length; i++) {
            sb.append(i == 0 ? "?" : "&");
            sb.append(params[i]).append("=").append(params[i + 1]);
        }

        // Add user
        sb.append(params.length < 2 ? "?" : "&");
        sb.append("user=").append(container.getUsername());

        // Add password
        // ignore blank password
        if (!StrUtil.isBlank(CLICKHOUSE_PASSWORD)) {
            sb.append("&password=").append(container.getPassword());
        }

        return sb.toString();
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
