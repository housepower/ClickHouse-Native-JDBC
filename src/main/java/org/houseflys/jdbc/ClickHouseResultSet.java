package org.houseflys.jdbc;

import java.sql.Array;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;

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
        return ((Number) block.getByPosition(columnIndex - 1).data(cursor)).byteValue();
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return ((Number) block.getByPosition(columnIndex - 1).data(cursor)).shortValue();
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return ((Number) block.getByPosition(columnIndex - 1).data(cursor)).intValue();
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return ((Number) block.getByPosition(columnIndex - 1).data(cursor)).longValue();
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return (String) block.getByPosition(columnIndex - 1).data(cursor);
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return ((Number) block.getByPosition(columnIndex - 1).data(cursor)).floatValue();
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return ((Number) block.getByPosition(columnIndex - 1).data(cursor)).doubleValue();
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        return (Array) block.getByPosition(columnIndex - 1).data(cursor);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return (Timestamp) block.getByPosition(columnIndex - 1).data(cursor);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return block.getByPosition(columnIndex - 1).data(cursor);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new ClickHouseResultSetMetaData(queryResponse.header());
    }

    private Block spliceHead() {
        return queryResponse.data().isEmpty() ? null : queryResponse.data().remove(0).block();
    }
}
