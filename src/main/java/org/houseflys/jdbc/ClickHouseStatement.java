package org.houseflys.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.houseflys.jdbc.protocol.QueryResponse;
import org.houseflys.jdbc.wrapper.SQLStatement;

public class ClickHouseStatement extends SQLStatement {

    private final ClickHouseConnection connection;

    public ClickHouseStatement(ClickHouseConnection connection) {
        this.connection = connection;
    }

    @Override
    public boolean execute(String query) throws SQLException {
        // TODO: return exists ResultSet
        return connection.sendQueryRequest(query) != null;
    }

    @Override
    public int executeUpdate(String query) throws SQLException {
        return connection.sendQueryRequest(query) == null ? 0 : 1;
    }

    @Override
    public ResultSet executeQuery(String query) throws SQLException {
        QueryResponse queryResponse = connection.sendQueryRequest(query);
        return new ClickHouseResultSet(queryResponse);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return super.getResultSet();
    }

    @Override
    public void close() throws SQLException {
    }
}
