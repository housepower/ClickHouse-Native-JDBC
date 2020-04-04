package com.github.housepower.jdbc.statement;

import com.github.housepower.jdbc.ClickHouseConnection;
import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.stream.ValuesWithParametersInputFormat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class ClickHousePreparedInsertStatement extends AbstractPreparedStatement {

    private final int posOfData;
    private final String fullQuery;
    private final String insertQuery;

    public ClickHousePreparedInsertStatement(int posOfData, String fullQuery,
                                             ClickHouseConnection conn) throws SQLException {
        super(conn, null);
        this.posOfData = posOfData;
        this.fullQuery = fullQuery;
        this.insertQuery = fullQuery.substring(0, posOfData);
        this.block = getSampleBlock(insertQuery);
        this.connection.sendInsertRequest(new Block());
        this.block.initWriteBuffer();
        new ValuesWithParametersInputFormat(fullQuery, posOfData).fillBlock(block);
    }

    @Override
    public boolean execute() throws SQLException {
        return executeQuery() != null;
    }

    @Override
    public int executeUpdate() throws SQLException {
        addParameters();
        getSampleBlock(insertQuery);
        int result = connection.sendInsertRequest(block);
        this.clearBatch();
        return result;
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
        block.setObject(index - 1, x);
    }

    private void addParameters() throws SQLException {
        block.appendRow();
    }

    @Override
    public void clearBatch() throws SQLException {
        this.block.initWriteBuffer();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        getSampleBlock(insertQuery);
        Integer rows = connection.sendInsertRequest(block);
        int[] result = new int[rows];
        Arrays.fill(result, -1);
        clearBatch();
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(": ");
        try {
            sb.append(insertQuery + " (");
            for (int i = 0; i < block.columns(); i++) {
                Object obj = block.getObject(i);
                if (obj == null) {
                    sb.append("?");
                } else if (obj instanceof Number) {
                    sb.append(obj);
                } else {
                    sb.append("'" + obj + "'");
                }
                if (i < block.columns() - 1) {
                    sb.append(",");
                }
            }
            sb.append(")");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
