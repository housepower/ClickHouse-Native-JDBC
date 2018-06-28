package com.github.housepower.jdbc.statement;

import com.github.housepower.jdbc.ClickHouseConnection;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.stream.ValuesWithParametersInputFormat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClickHousePreparedInsertStatement extends AbstractPreparedStatement {

    private final int posOfData;
    private final String fullQuery;
    private final String insertQuery;
    private final List<Object[]> parameters;

    public ClickHousePreparedInsertStatement(int posOfData, String fullQuery, ClickHouseConnection conn)
        throws SQLException {
        super(conn, null, computeQuestionMarkSize(fullQuery, posOfData));
        this.posOfData = posOfData;
        this.fullQuery = fullQuery;
        this.parameters = new ArrayList<Object[]>();
        this.insertQuery = fullQuery.substring(0, posOfData);
    }

    @Override
    public boolean execute() throws SQLException {
        return executeQuery() != null;
    }

    @Override
    public int executeUpdate() throws SQLException {
        parameters.add(super.parameters);
        return connection.sendInsertRequest(insertQuery,
            new ValuesWithParametersInputFormat(fullQuery, posOfData, parameters));
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        executeUpdate();
        return null;
    }

    @Override
    public void addBatch() throws SQLException {
        parameters.add(super.parameters);
        super.parameters = new Object[super.parameters.length];
    }

    @Override
    public void clearBatch() throws SQLException {
        parameters.clear();
        super.parameters = new Object[super.parameters.length];
    }

    @Override
    public int[] executeBatch() throws SQLException {
        Integer rows = connection.sendInsertRequest(
            insertQuery, new ValuesWithParametersInputFormat(fullQuery, posOfData, parameters));
        int[] result = new int[rows];
        Arrays.fill(result, -1);
        return result;
    }

    private static int computeQuestionMarkSize(String query, int start) throws SQLException {
        int param = 0;
        boolean inQuotes = false, inBackQuotes = false;
        for (int i = 0; i < query.length(); i++) {
            char ch = query.charAt(i);
            if (ch == '`') {
                inBackQuotes = !inBackQuotes;
            } else if (ch == '\'') {
                inQuotes = !inQuotes;
            } else if (!inBackQuotes && !inQuotes) {
                if (ch == '?') {
                    Validate.isTrue(i > start, "");
                    param++;
                }
            }
        }
        return param;
    }
}
