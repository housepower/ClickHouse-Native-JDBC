package org.houseflys.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.houseflys.jdbc.protocol.QueryResponse;
import org.houseflys.jdbc.type.Block;
import org.houseflys.jdbc.wrapper.SQLResultSet;

public class ClickHouseResultSet extends SQLResultSet {

    private Block block;
    private final QueryResponse queryResponse;

    private int cursor = -1;

    public ClickHouseResultSet(QueryResponse queryResponse) {
        this.queryResponse = queryResponse;
        this.block = spliceHead();
    }

    @Override
    public boolean next() throws SQLException {
        cursor++;

        for (; cursor >= block.rows(); cursor = -1, cursor++) {
            if ((block = spliceHead()) == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return (Byte) block.getByPosition(columnIndex - 1).data(cursor);
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return (Short) block.getByPosition(columnIndex - 1).data(cursor);
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return (Integer) block.getByPosition(columnIndex - 1).data(cursor);
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return (Long) block.getByPosition(columnIndex - 1).data(cursor);
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return (String) block.getByPosition(columnIndex - 1).data(cursor);
    }

    private Block spliceHead() {
        return queryResponse.data().isEmpty() ? null : queryResponse.data().remove(0).block();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new ClickHouseResultSetMetaData(queryResponse.header());
    }
}
