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
        for (int i = 0 ; i < length; i ++) {
            result[i] = data[i+offset];
        }
        return new ClickHouseArray(result);
    }
}
