package com.github.housepower.jdbc.wrapper;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Struct;
import java.util.Map;

public class SQLStruct implements Struct {
    @Override
    public String getSQLTypeName() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Object[] getAttributes() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
