package com.github.housepower.jdbc.tool;

import org.mockito.Mockito;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class EmbeddedDriver implements Driver {

    public static final Connection MOCKED_CONNECTION = Mockito.mock(Connection.class);
    public static final String EMBEDDED_DRIVER_PREFIX = "jdbc:embedded:";

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return MOCKED_CONNECTION;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith(EMBEDDED_DRIVER_PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
