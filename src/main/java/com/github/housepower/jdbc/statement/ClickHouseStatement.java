package com.github.housepower.jdbc.statement;

import com.github.housepower.jdbc.ClickHouseConnection;
import com.github.housepower.jdbc.ClickHouseResultSet;
import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.protocol.QueryResponse;
import com.github.housepower.jdbc.settings.ClickHouseConfig;
import com.github.housepower.jdbc.settings.SettingKey;
import com.github.housepower.jdbc.stream.ValuesInputFormat;
import com.github.housepower.jdbc.wrapper.SQLStatement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickHouseStatement extends SQLStatement {
    private static final Pattern
        VALUES_REGEX =
        Pattern.compile("[V|v][A|a][L|l][U|u][E|e][S|s]\\s*\\(");

    private ResultSet lastResultSet;
    protected Block block;
    protected final ClickHouseConnection connection;

    private long maxRows;
    private ClickHouseConfig cfg;


    public ClickHouseStatement(ClickHouseConnection connection) {
        this.connection = connection;
        this.cfg = connection.getConfigure().copy();
    }

    @Override
    public boolean execute(String query) throws SQLException {
        return executeQuery(query) != null;
    }

    @Override
    public ResultSet executeQuery(String query) throws SQLException {
        executeUpdate(query);
        return getResultSet();
    }

    @Override
    public int executeUpdate(String query) throws SQLException {
        cfg.settings().put(SettingKey.max_result_rows, maxRows);

        Matcher matcher = VALUES_REGEX.matcher(query);
        if (matcher.find()) {
            lastResultSet = null;
            String insertQuery = query.substring(0, matcher.end() - 1);
            block = getSampleBlock(insertQuery);
            block.initWriteBuffer();
            new ValuesInputFormat(matcher.end() - 1, query).fillBlock(block);
            return connection.sendInsertRequest(block);
        }

        QueryResponse response = connection.sendQueryRequest(query, cfg);
        lastResultSet = new ClickHouseResultSet(response.header(), response.data().get(), this);
        return 0;
    }

    public Block getSampleBlock(final String insertQuery) throws SQLException {
        return connection.getSampleBlock(insertQuery);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return (int) maxRows;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        if (max < 0) {
            throw new SQLException(String.format(Locale.ROOT, "Illegal maxRows value: %d", max));
        }
        maxRows = max * 1L;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
    }

    @Override
    public void close() throws SQLException {
    }

    // JDBC returns timeout in seconds
    @Override
    public int getQueryTimeout() {
        return cfg.queryTimeout() / 1000;
    }

    @Override
    public void setQueryTimeout(int seconds) {
        cfg.setQueryTimeout(seconds * 1000);
    }

    @Override
    public ResultSet getResultSet() {
        return lastResultSet;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return lastResultSet.getMetaData();
    }

    // TODO cancel
    @Override
    public void cancel() {
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String name) throws SQLException {

    }

    @Override
    public int getUpdateCount() throws SQLException {
        return 0;
    }

    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }


    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }

    @Override
    public void addBatch(String sql) throws SQLException {

    }

    @Override
    public void clearBatch() throws SQLException {

    }

    @Override
    public int[] executeBatch() throws SQLException {
        return new int[0];
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
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
