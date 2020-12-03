/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.housepower.jdbc;

import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.data.IColumn;
import com.github.housepower.jdbc.misc.CheckedIterator;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.protocol.DataResponse;
import com.github.housepower.jdbc.settings.ClickHouseConfig;
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

public class ClickHouseResultSet implements SQLResultSet {
    private int currentRowNum = -1;
    private Block currentBlock = new Block();

    private int lastFetchRowIdx = -1;
    private int lastFetchColumnIdx = -1;
    private Block lastFetchBlock = null;

    private final ClickHouseStatement statement;
    private final ClickHouseConfig cfg;
    private final String db;
    private final String table;
    private final Block header;
    private final CheckedIterator<DataResponse, SQLException> dataResponses;

    public ClickHouseResultSet(ClickHouseStatement statement,
                               ClickHouseConfig cfg,
                               String db,
                               String table,
                               Block header,
                               CheckedIterator<DataResponse, SQLException> dataResponses) {
        this.statement = statement;
        this.cfg = cfg;
        this.db = db;
        this.table = table;
        this.header = header;
        this.dataResponses = dataResponses;
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
    public byte[] getBytes(String name) throws SQLException {
        return this.getBytes(this.findColumn(name));
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
        Object data = getObject(index);
        if (data == null) {
            return 0;
        }
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
        Object data = getObject(index);
        if (data == null) {
            return 0;
        }
        return ((Number) data).byteValue();
    }

    @Override
    public Date getDate(int index) throws SQLException {
        Object data = getObject(index);
        return (Date) data;
    }

    @Override
    public long getLong(int index) throws SQLException {
        Object data = getObject(index);
        if (data == null) {
            return 0;
        }
        return ((Number) data).longValue();
    }

    @Override
    public Array getArray(int index) throws SQLException {
        Object data = getObject(index);
        return (Array) data;
    }

    @Override
    public float getFloat(int index) throws SQLException {
        Object data = getObject(index);
        if (data == null) {
            return 0;
        }
        return ((Number) data).floatValue();
    }

    @Override
    public short getShort(int index) throws SQLException {
        Object data = getObject(index);
        if (data == null) {
            return 0;
        }
        return ((Number) data).shortValue();
    }

    @Override
    public double getDouble(int index) throws SQLException {
        Object data = getObject(index);
        if (data == null) {
            return 0;
        }
        return ((Number) data).doubleValue();
    }

    @Override
    public String getString(int index) throws SQLException {
        Object data = getObject(index);
        return (String) data;
    }

    @Override
    public byte[] getBytes(int index) throws SQLException {
        String data = (String) getObject(index);
        return data.getBytes(cfg.charset());
    }

    @Override
    public Object getObject(int index) throws SQLException {
        Validate.isTrue(currentRowNum >= 0 && currentRowNum < currentBlock.rowCnt(),
                "No row information was obtained.You must call ResultSet.next() before that.");
        IColumn column = (lastFetchBlock = currentBlock).getColumnByPosition((lastFetchColumnIdx = index - 1));
        return column.value((lastFetchRowIdx = currentRowNum));
    }

    @Override
    public Timestamp getTimestamp(int index) throws SQLException {
        Object data = getObject(index);
        return (Timestamp) data;
    }

    @Override
    public BigDecimal getBigDecimal(int index) throws SQLException {
        Object data = getObject(index);
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
        Validate.isTrue(lastFetchColumnIdx >= 0, "Please call Result.getXXX()");
        Validate.isTrue(lastFetchRowIdx >= 0 && lastFetchRowIdx < lastFetchBlock.rowCnt(), "Please call Result.next()");
        return lastFetchBlock.getColumnByPosition(lastFetchColumnIdx).value(lastFetchRowIdx) == null;
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
        return new ClickHouseResultSetMetaData(header, db, table);
    }

    @Override
    public boolean next() throws SQLException {
        return ++currentRowNum < currentBlock.rowCnt() || (currentRowNum = 0) < (currentBlock = fetchBlock()).rowCnt();
    }

    private Block fetchBlock() throws SQLException {
        while (dataResponses.hasNext()) {
            DataResponse next = dataResponses.next();
            if (next.block().rowCnt() > 0) {
                return next.block();
            }
        }
        return new Block();
    }
}
