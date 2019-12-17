package com.github.housepower.jdbc.statement;

import com.github.housepower.jdbc.ClickHouseConnection;
import com.github.housepower.jdbc.misc.Validate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class ClickHousePreparedInsertStatement extends AbstractPreparedStatement {

    private final int posOfData;
    private final String fullQuery;
    private final String insertQuery;

    public ClickHousePreparedInsertStatement(int posOfData, String fullQuery, ClickHouseConnection conn)
        throws SQLException {
        super(conn, null);

        this.posOfData = posOfData;
        this.fullQuery = fullQuery;
        this.insertQuery = fullQuery.substring(0, posOfData);

        this.block = getSampleBlock(insertQuery);
        this.block.initWriteBuffer();
    }

    @Override
    public boolean execute() throws SQLException {
        return executeQuery() != null;
    }

    @Override
    public int executeUpdate() throws SQLException {
		addParameters();
		return connection.sendInsertRequest(block);
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        executeUpdate();
        return null;
    }

    @Override
    public void addBatch() throws SQLException {
		addParameters();
    }

    @Override
    public void setObject(int index, Object x) throws SQLException {
        block.setObject(index, x);
    }

    private void addParameters() throws SQLException {
        block.appendRow();
	}

    @Override
    public void clearBatch() throws SQLException {
    }

    @Override
    public int[] executeBatch() throws SQLException {
        Integer rows = connection.sendInsertRequest(block);
        int[] result = new int[rows];
        Arrays.fill(result, -1);
        clearBatch();
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
