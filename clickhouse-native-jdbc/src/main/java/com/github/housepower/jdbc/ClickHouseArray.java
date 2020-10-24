package com.github.housepower.jdbc;

import com.github.housepower.jdbc.wrapper.SQLArray;

import java.sql.SQLException;

public class ClickHouseArray extends SQLArray {
    private final Object[] data;

    public ClickHouseArray(Object[] data) {
        this.data = data;
    }

    @Override
    public void free() throws SQLException {
    }

    @Override
    public Object getArray() throws SQLException {
        return data;
    }

    public ClickHouseArray slice(int offset, int length) {
        Object []result = new Object[length];
        if (length >= 0) System.arraycopy(data, offset, result, 0, length);
        return new ClickHouseArray(result);
    }
}
