package com.github.housepower.jdbc.statement;


import com.github.housepower.jdbc.ClickHouseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;

public class ClickHousePreparedQueryStatement extends AbstractPreparedStatement {

    public ClickHousePreparedQueryStatement(ClickHouseConnection conn, String query) {
        this(conn, splitQueryByQuestionMark(query));
    }

    private ClickHousePreparedQueryStatement(ClickHouseConnection conn, String[] parts) {
        super(conn, parts);
    }

    @Override
    public boolean execute() throws SQLException {
        return execute(assembleQueryPartsAndParameters());
    }

    @Override
    public int executeUpdate() throws SQLException {
        return executeUpdate(assembleQueryPartsAndParameters());
    }

    @Override
    public int[] executeBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException("");
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return executeQuery(assembleQueryPartsAndParameters());
    }

    @Override
    public void setObject(int index, Object x) throws SQLException {
        parameters[index - 1] = x;
    }

    private static String[] splitQueryByQuestionMark(String query) {
        int lastPos = 0;
        List<String> queryParts = new ArrayList<String>();
        boolean inQuotes = false, inBackQuotes = false;
        for (int i = 0; i < query.length(); i++) {
            char ch = query.charAt(i);
            if (ch == '`') {
                inBackQuotes = !inBackQuotes;
            } else if (ch == '\'') {
                inQuotes = !inQuotes;
            } else if (!inBackQuotes && !inQuotes) {
                if (ch == '?') {
                    queryParts.add(query.substring(lastPos, i));
                    lastPos = i + 1;
                }
            }
        }
        queryParts.add(query.substring(lastPos, query.length()));
        return queryParts.toArray(new String[queryParts.size()]);
    }
    
    public String toString(){
    	StringBuilder queryBuilder=new StringBuilder();
    	queryBuilder.append(super.toString());
    	try {
			queryBuilder.append(": ");
			queryBuilder.append(assembleQueryPartsAndParameters());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return queryBuilder.toString();
    }
}
