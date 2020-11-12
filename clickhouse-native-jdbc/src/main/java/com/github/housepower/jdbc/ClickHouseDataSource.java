package com.github.housepower.jdbc;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * ClickHouseDataSource
 * This file is copied from clickhouse-jdbc
 */
public class ClickHouseDataSource implements DataSource {
    protected final ClickHouseDriver driver = new ClickHouseDriver();
    protected final String url;
    protected PrintWriter printWriter;
    protected int loginTimeoutSeconds = 0;
    private Properties properties;

    public ClickHouseDataSource(String url) throws SQLException {
        this.url = url;
        this.properties = new Properties();
    }

    public ClickHouseDataSource(String url, Properties info) throws SQLException {
        this.url = url;
        this.properties = info;
    }


    @Override
    public ClickHouseConnection getConnection() throws SQLException {
        return driver.connect(url, properties);
    }

    @Override
    public ClickHouseConnection getConnection(String username, String password) throws SQLException {
        Properties info = new Properties();
        info.putAll(properties);
        info.put("user", username);
        info.put("password", password);
        return driver.connect(url, properties);
    }


    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return printWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        printWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        loginTimeoutSeconds = seconds;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return loginTimeoutSeconds;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(getClass())) {
            return iface.cast(this);
        }
        throw new SQLException("Cannot unwrap to " + iface.getName());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(getClass());
    }
}
