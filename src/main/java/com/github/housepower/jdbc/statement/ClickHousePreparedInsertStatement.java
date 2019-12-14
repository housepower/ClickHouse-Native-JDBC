package com.github.housepower.jdbc.statement;

import com.github.housepower.jdbc.ClickHouseConnection;
import com.github.housepower.jdbc.misc.Slice;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.stream.ValuesWithParametersInputFormat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class ClickHousePreparedInsertStatement extends AbstractPreparedStatement {

    private final int posOfData;
    private final String fullQuery;
    private final String insertQuery;
    private final Slice []columns;

	private int maxRows = 0;

    public ClickHousePreparedInsertStatement(int posOfData, String fullQuery, ClickHouseConnection conn)
        throws SQLException {
        super(conn, null, computeQuestionMarkSize(fullQuery, posOfData));

    	int colSize = computeQuestionMarkSize(fullQuery, posOfData);
        this.posOfData = posOfData;
        this.fullQuery = fullQuery;
        this.insertQuery = fullQuery.substring(0, posOfData);

        // extract the colSize
        this.columns = new Slice[colSize];
        for (int i = 0; i < colSize; ++i) {
        	this.columns[i] = new Slice(8192);
		}
    }

    @Override
    public boolean execute() throws SQLException {
        return executeQuery() != null;
    }

    @Override
    public int executeUpdate() throws SQLException {
		addParameters();
		return connection.sendInsertRequest(insertQuery,
            new ValuesWithParametersInputFormat(fullQuery, posOfData, columns, maxRows));
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

    private void addParameters() throws SQLException {
    	//ensure rows size
		for (int i = 0; i < super.parameters.length; i++) {
		    columns[i].add(super.parameters[i]);
		}
		maxRows ++;
	}

    @Override
    public void clearBatch() throws SQLException {
    	maxRows = 0;
    	for (int i = 0; i < columns.length; i++) {
    	    columns[i] = columns[i].sub(0,0);
        }
    }

    @Override
    public int[] executeBatch() throws SQLException {
        Integer rows = connection.sendInsertRequest(
            insertQuery, new ValuesWithParametersInputFormat(fullQuery, posOfData, columns, maxRows));
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
