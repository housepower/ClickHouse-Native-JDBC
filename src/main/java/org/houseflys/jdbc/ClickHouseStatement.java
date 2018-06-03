package org.houseflys.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.houseflys.jdbc.stream.ValuesInputFormat;
import org.houseflys.jdbc.wrapper.SQLStatement;

public class ClickHouseStatement extends SQLStatement {
    private static final Pattern VALUES_REGEX = Pattern.compile("[V|v][A|a][L|l][U|u][E|e][S|s]\\s*\\(");

    private final ClickHouseConnection connection;

    private ResultSet lastResultSet;

    public ClickHouseStatement(ClickHouseConnection connection) {
        this.connection = connection;
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
        Matcher matcher = VALUES_REGEX.matcher(query);
        if (matcher.find()) {
            String insertQuery = query.substring(0, matcher.end() - 1);
            ValuesInputFormat inputFormat = new ValuesInputFormat(matcher.end() - 1, query);
            Integer rows = connection.sendInsertRequest(insertQuery, inputFormat);
            lastResultSet = null;
            return rows;
        }
        lastResultSet = new ClickHouseResultSet(connection.sendQueryRequest(query));
        return 0;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return lastResultSet;
    }

    @Override
    public void close() throws SQLException {
    }
}
