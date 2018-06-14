package com.github.housepower.jdbc;

import com.github.housepower.jdbc.settings.ClickHouseConfig;
import com.github.housepower.jdbc.settings.ClickHouseDefines;
import com.github.housepower.jdbc.settings.SettingKey;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class NonRegisterDriver implements Driver {

    private static final String JDBC_PREFIX = "jdbc:";
    private static final String CLICK_HOUSE_JDBC_PREFIX = JDBC_PREFIX + "clickhouse:";

    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith(CLICK_HOUSE_JDBC_PREFIX);
    }

    public Connection connect(String url, Properties properties) throws SQLException {
        ClickHouseConfig configure = new ClickHouseConfig(url, properties);
        return ClickHouseConnection.createClickHouseConnection(configure);
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties properties) throws SQLException {
        ClickHouseConfig configure = new ClickHouseConfig(url, properties);

        int index = 0;
        DriverPropertyInfo[] driverPropertiesInfo = new DriverPropertyInfo[configure.settings().size()];

        for (Map.Entry<SettingKey, Object> entry : configure.settings().entrySet()) {
            String value = String.valueOf(entry.getValue());

            DriverPropertyInfo property = new DriverPropertyInfo(entry.getKey().name(), value);
            property.description = entry.getKey().describe();

            driverPropertiesInfo[index++] = property;
        }

        return driverPropertiesInfo;
    }

    public int getMajorVersion() {
        return ClickHouseDefines.MAJOR_VERSION;
    }

    public int getMinorVersion() {
        return ClickHouseDefines.MINOR_VERSION;
    }

    public boolean jdbcCompliant() {
        return false;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
