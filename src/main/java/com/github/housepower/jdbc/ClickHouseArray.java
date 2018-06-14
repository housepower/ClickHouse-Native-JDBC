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
}
