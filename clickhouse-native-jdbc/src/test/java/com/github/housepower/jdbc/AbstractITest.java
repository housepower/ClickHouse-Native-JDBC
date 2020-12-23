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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Enumeration;

import javax.sql.DataSource;

public abstract class AbstractITest implements Serializable {

    private static final String DEFAULT_USERNAME = "default";

    protected static final ZoneId CLIENT_TZ = ZoneId.systemDefault();
    protected static final ZoneId SERVER_TZ = ZoneId.of("UTC");
    protected static final String DRIVER_CLASS_NAME = "com.github.housepower.jdbc.ClickHouseDriver";
    protected static final int SERVER_PORT = Integer.parseInt(System.getProperty("CLICK_HOUSE_SERVER_PORT", "9000"));
    protected static final String SERVER_USER = System.getProperty("CLICK_HOUSE_SERVER_USER", DEFAULT_USERNAME);
    protected static final String SERVER_PASSWORD = System.getProperty("CLICK_HOUSE_SERVER_PASSWORD", null);

    /**
     * just for compatible with scala
     */
    protected String getJdbcUrl() {
        return getJdbcUrl("");
    }

    protected String getJdbcUrl(Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:clickhouse://127.0.0.1:").append(SERVER_PORT);
        for (int i = 0; i + 1 < params.length; i++) {
            if (i == 0) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            sb.append(params[i]).append("=").append(params[i + 1]);
        }
        if (!SERVER_USER.equals(DEFAULT_USERNAME)) {
            // Add user
            sb.append(params.length == 0 ? "?" : "&");
            sb.append("user=").append(SERVER_USER);
            if (SERVER_PASSWORD != null) {
                // Add password
                sb.append("&");
                sb.append("password=").append(SERVER_PASSWORD);
            }
        }
        else {
            if (SERVER_PASSWORD != null) {
                // Add password
                sb.append(params.length == 0 ? "?" : "&");
                sb.append("password=").append(SERVER_PASSWORD);
            }
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

    @FunctionalInterface
    public interface WithConnection {
        void apply(Connection connection) throws Exception;
    }
}
