package org.houseflys.jdbc;

import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Types;

import org.houseflys.jdbc.type.Block;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.wrapper.SQLResultSetMetaData;

public class ClickHouseResultSetMetaData extends SQLResultSetMetaData {

    private final Block header;

    public ClickHouseResultSetMetaData(Block header) {
        this.header = header;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return (int) this.header.columns();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return this.header.getByPosition(column - 1).name();
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return this.header.getByPosition(column - 1).name();
    }

    @Override
    public int getColumnType(int idx) throws SQLException {
        Column column = header.getByPosition(idx - 1);
        return Types.NULL;
        //        return super.getColumnType(column);
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return false;
    }
}
