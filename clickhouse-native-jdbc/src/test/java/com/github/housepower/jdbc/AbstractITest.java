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
import java.time.ZoneId;
import java.util.Enumeration;

public abstract class AbstractITest implements Serializable {

    protected static ZoneId clientTz = ZoneId.systemDefault();
    protected static ZoneId serverTz = ZoneId.of("UTC");

    protected static final int SERVER_PORT = Integer.parseInt(System.getProperty("CLICK_HOUSE_SERVER_PORT", "9000"));

    protected String getJdbcUrl() {
        return getJdbcUrl(false);
    }

    protected String getJdbcUrl(boolean useClientTz) {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:clickhouse://127.0.0.1:").append(SERVER_PORT);
        if (useClientTz) {
            sb.append("?use_client_time_zone=true");
        }
        return sb.toString();
    }

    // this method should be synchronized since we reset the registered drivers in DriverManager
    synchronized protected void withNewConnection(WithConnection withConnection, Object... args) throws Exception {
        // remove all registered jdbc drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            DriverManager.deregisterDriver(drivers.nextElement());
        }
        DriverManager.registerDriver(new ClickHouseDriver());

        String connectionStr;
        if (args.length > 0) {
            // first arg is use_client_time_zone
            connectionStr = getJdbcUrl(args[0].equals(true));
        } else {
            connectionStr = getJdbcUrl();
        }
        try (Connection connection = DriverManager.getConnection(connectionStr)) {
            withConnection.apply(connection);
        }
    }

    @FunctionalInterface
    public interface WithConnection {
        void apply(Connection connection) throws Exception;
    }
}
