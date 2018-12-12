package com.github.housepower.jdbc;

import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.data.Column;
import com.github.housepower.jdbc.misc.CheckedIterator;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.protocol.DataResponse;
import com.github.housepower.jdbc.statement.ClickHouseStatement;
import com.github.housepower.jdbc.wrapper.SQLResultSet;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;

public class ClickHouseResultSet extends SQLResultSet {
    private int row = -1;
    private Block current = new Block();

    private int lastFetchRow = -1;
    private int lastFetchColumn = -1;
    private Block lastFetchBlock = null;

    private final Block header;
    private final ClickHouseStatement statement;
    private final CheckedIterator<DataResponse, SQLException> iterator;

    public ClickHouseResultSet(Block header, CheckedIterator<DataResponse, SQLException> iterator, ClickHouseStatement statement) {
        this.header = header;
        this.iterator = iterator;
        this.statement = statement;
    }

    @Override
    public int getInt(String name) throws SQLException {
        return this.getInt(this.findColumn(name));
    }

    @Override
    public URL getURL(String name) throws SQLException {
        return this.getURL(this.findColumn(name));
    }

    @Override
    public byte getByte(String name) throws SQLException {
        return this.getByte(this.findColumn(name));
    }

    @Override
    public Date getDate(String name) throws SQLException {
        return this.getDate(this.findColumn(name));
    }

    @Override
    public long getLong(String name) throws SQLException {
        return this.getLong(this.findColumn(name));
    }

    @Override
    public Array getArray(String name) throws SQLException {
        return this.getArray(this.findColumn(name));
    }

    @Override
    public float getFloat(String name) throws SQLException {
        return this.getFloat(this.findColumn(name));
    }

    @Override
    public short getShort(String name) throws SQLException {
        return this.getShort(this.findColumn(name));
    }

    @Override
    public double getDouble(String name) throws SQLException {
        return this.getDouble(this.findColumn(name));
    }

    @Override
    public String getString(String name) throws SQLException {
        return this.getString(this.findColumn(name));
    }

    @Override
    public Object getObject(String name) throws SQLException {
        return this.getObject(this.findColumn(name));
    }

    @Override
    public Timestamp getTimestamp(String name) throws SQLException {
        return this.getTimestamp(this.findColumn(name));
    }

    @Override
    public BigDecimal getBigDecimal(String name) throws SQLException {
        return this.getBigDecimal(this.findColumn(name));
    }

    /*===================================================================*/

    @Override
    public int getInt(int index) throws SQLException {
        Object data;
        Validate.isTrue((data = getObject(index)) instanceof Number);
        return ((Number) data).intValue();
    }

    @Override
    public URL getURL(int index) throws SQLException {
        try {
            return new URL(this.getString(index));
        } catch (MalformedURLException ex) {
            throw new SQLException(ex.getMessage(), ex);
        }
    }

    @Override
    public byte getByte(int index) throws SQLException {
        Object data;
        Validate.isTrue((data = getObject(index)) instanceof Number);
        return ((Number) data).byteValue();
    }

    @Override
    public Date getDate(int index) throws SQLException {
        Object data;
        Validate.isTrue((data = getObject(index)) instanceof Date);
        return (Date) data;
    }

    @Override
    public Date getDate(int index, Calendar cal) throws SQLException {
        return super.getDate(index, cal);
    }

    @Override
    public long getLong(int index) throws SQLException {
        Object data;
        Validate.isTrue((data = getObject(index)) instanceof Number);
        return ((Number) data).longValue();
    }

    @Override
    public Array getArray(int index) throws SQLException {
        Object data;
        Validate.isTrue((data = getObject(index)) instanceof Array);
        return (Array) data;
    }

    @Override
    public float getFloat(int index) throws SQLException {
        Object data;
        Validate.isTrue((data = getObject(index)) instanceof Number);
        return ((Number) data).floatValue();
    }

    @Override
    public short getShort(int index) throws SQLException {
        Object data;
        Validate.isTrue((data = getObject(index)) instanceof Number);
        return ((Number) data).shortValue();
    }

    @Override
    public double getDouble(int index) throws SQLException {
        Object data;
        Validate.isTrue((data = getObject(index)) instanceof Number);
        return ((Number) data).doubleValue();
    }

    @Override
    public String getString(int index) throws SQLException {
        Object data;
        Validate.isTrue((data = getObject(index)) instanceof String);
        return (String) data;
    }

    @Override
    public Object getObject(int index) throws SQLException {
        Validate.isTrue(row >= 0 && row < current.rows(),
            "No row information was obtained.You must call ResultSet.next() before that.");
        Column column = (lastFetchBlock = current).getByPosition((lastFetchColumn = index - 1));
        Object rowData = column.data((lastFetchRow = row));
        return rowData == null ? column.type().defaultValue() : rowData;
    }

    @Override
    public Timestamp getTimestamp(int index) throws SQLException {
        Object data;
        Validate.isTrue((data = getObject(index)) instanceof Timestamp);
        return (Timestamp) data;
    }

    @Override
    public BigDecimal getBigDecimal(int index) throws SQLException {
        Object data;
        Validate.isTrue((data = getObject(index)) instanceof Number);
        return new BigDecimal(data.toString());
    }

    /*==================================================================*/

    @Override
    public void close() throws SQLException {
        // nothing
    }

    @Override
    public boolean wasNull() throws SQLException {
        Validate.isTrue(lastFetchBlock != null, "Please call Result.next()");
        Validate.isTrue(lastFetchColumn >= 0, "Please call Result.getXXX()");
        Validate.isTrue(lastFetchRow >= 0 && lastFetchRow < lastFetchBlock.rows(), "Please call Result.next()");
        return lastFetchBlock.getByPosition(lastFetchColumn).data(lastFetchRow) == null;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public Statement getStatement() throws SQLException {
        return statement;
    }

    @Override
    public int findColumn(String name) throws SQLException {
        return header.getPositionByName(name);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new ClickHouseResultSetMetaData(header);
    }

    @Override
    public boolean next() throws SQLException {
        return ++row < current.rows() || (row = 0) < (current = fetchBlock()).rows();
    }

    private Block fetchBlock() throws SQLException {
        while (iterator.hasNext()) {
            DataResponse next = iterator.next();
            if (next.block().rows() > 0) {
                return next.block();
            }
        }
        return new Block();
    }
}
