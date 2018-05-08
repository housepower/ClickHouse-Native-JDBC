package org.houseflys.jdbc;

import org.houseflys.jdbc.settings.ClickHouseDefines;
import org.houseflys.jdbc.settings.ClickHouseURL;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class NonRegisterDriver implements Driver {

    public boolean acceptsURL(String url) throws SQLException {
        return new ClickHouseURL(url).accept();
    }

    public Connection connect(String url, Properties properties) throws SQLException {
        return new ClickHouseConnection(url, properties);
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    public int getMajorVersion() {
        return ClickHouseDefines.DBMS_VERSION_MAJOR.intValue();
    }

    public int getMinorVersion() {
        return ClickHouseDefines.DBMS_VERSION_MINOR.intValue();
    }

    public boolean jdbcCompliant() {
        return false;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
