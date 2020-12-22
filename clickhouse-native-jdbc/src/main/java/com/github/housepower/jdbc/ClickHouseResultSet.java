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
import com.github.housepower.jdbc.log.Logger;
import com.github.housepower.jdbc.log.LoggerFactory;
import com.github.housepower.jdbc.misc.CheckedIterator;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.protocol.DataResponse;
import com.github.housepower.jdbc.settings.ClickHouseConfig;
import com.github.housepower.jdbc.statement.ClickHouseStatement;
import com.github.housepower.jdbc.wrapper.SQLResultSet;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

public class ClickHouseResultSet implements SQLResultSet {

    private static final Logger LOG = LoggerFactory.getLogger(ClickHouseResultSet.class);

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

    private boolean isFirst = false;
    private boolean isAfterLast = false;
    private boolean isClosed = false;

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
    public boolean getBoolean(String name) throws SQLException {
        return this.getBoolean(this.findColumn(name));
    }

    @Override
    public byte getByte(String name) throws SQLException {
        return this.getByte(this.findColumn(name));
    }

    @Override
    public short getShort(String name) throws SQLException {
        return this.getShort(this.findColumn(name));
    }

    @Override
    public int getInt(String name) throws SQLException {
        return this.getInt(this.findColumn(name));
    }

    @Override
    public long getLong(String name) throws SQLException {
        return this.getLong(this.findColumn(name));
    }

    @Override
    public float getFloat(String name) throws SQLException {
        return this.getFloat(this.findColumn(name));
    }

    @Override
    public double getDouble(String name) throws SQLException {
        return this.getDouble(this.findColumn(name));
    }

    @Override
    public Timestamp getTimestamp(String name) throws SQLException {
        return this.getTimestamp(this.findColumn(name));
    }

    @Override
    public Date getDate(String name) throws SQLException {
        return this.getDate(this.findColumn(name));
    }

    @Override
    public BigDecimal getBigDecimal(String name) throws SQLException {
        return this.getBigDecimal(this.findColumn(name));
    }

    @Override
    public String getString(String name) throws SQLException {
        return this.getString(this.findColumn(name));
    }

    @Override
    public byte[] getBytes(String name) throws SQLException {
        return this.getBytes(this.findColumn(name));
    }

    @Override
    public URL getURL(String name) throws SQLException {
        return this.getURL(this.findColumn(name));
    }

    @Override
    public Array getArray(String name) throws SQLException {
        return this.getArray(this.findColumn(name));
    }

    @Override
    public Object getObject(String name) throws SQLException {
        return this.getObject(this.findColumn(name));
    }

    @Override
    public boolean getBoolean(int index) throws SQLException {
        Object data = getObject(index);
        if (data == null) {
            return false;
        }
        Number ndata = (Number) data;
        return (ndata.shortValue() == 1);
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
    public short getShort(int index) throws SQLException {
        Object data = getObject(index);
        if (data == null) {
            return 0;
        }
        return ((Number) data).shortValue();
    }

    @Override
    public int getInt(int index) throws SQLException {
        Object data = getObject(index);
        if (data == null) {
            return 0;
        }
        return ((Number) data).intValue();
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
    public float getFloat(int index) throws SQLException {
        Object data = getObject(index);
        if (data == null) {
            return 0;
        }
        return ((Number) data).floatValue();
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
    public Timestamp getTimestamp(int index) throws SQLException {
        Object data = getObject(index);
        return (Timestamp) data;
    }

    @Override
    public Date getDate(int index) throws SQLException {
        Object data = getObject(index);
        return (Date) data;
    }

    @Override
    public BigDecimal getBigDecimal(int index) throws SQLException {
        Object data = getObject(index);
        if (data == null) {
            return null;
        }
        return new BigDecimal(data.toString());
    }

    @Override
    public String getString(int index) throws SQLException {
        Object data = getObject(index);
        if (data == null) {
            return null;
        }
        // TODO format by IDataType
        return data.toString();
    }

    @Override
    public byte[] getBytes(int index) throws SQLException {
        String data = (String) getObject(index);
        if (data == null) {
            return null;
        }
        return data.getBytes(cfg.charset());
    }

    @Override
    public URL getURL(int index) throws SQLException {
        String data = this.getString(index);
        if (data == null) {
            return null;
        }
        try {
            return new URL(data);
        } catch (MalformedURLException ex) {
            throw new SQLException(ex.getMessage(), ex);
        }
    }

    @Override
    public Array getArray(int index) throws SQLException {
        Object data = getObject(index);
        return (Array) data;
    }

    @Override
    public Object getObject(int index) throws SQLException {
        LOG.trace("get object at row: {}, column: {} from block with column count: {}, row count: {}",
                currentRowNum, index, currentBlock.columnCnt(), currentBlock.rowCnt());
        Validate.isTrue(currentRowNum >= 0 && currentRowNum < currentBlock.rowCnt(),
                "No row information was obtained. You must call ResultSet.next() before that.");
        IColumn column = (lastFetchBlock = currentBlock).getColumnByPosition((lastFetchColumnIdx = index - 1));
        return column.value((lastFetchRowIdx = currentRowNum));
    }

    @Override
    public boolean first() throws SQLException {
        throw new SQLException("TYPE_FORWARD_ONLY");
    }

    @Override
    public boolean last() throws SQLException {
        throw new SQLException("TYPE_FORWARD_ONLY");
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return currentRowNum == -1;
    }

    @Override
    public boolean isFirst() throws SQLException {
        return isFirst;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return isAfterLast;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
    }

    @Override
    public int getFetchSize() throws SQLException {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new ClickHouseResultSetMetaData(header, db, table);
    }

    @Override
    public boolean wasNull() throws SQLException {
        Validate.isTrue(lastFetchBlock != null, "Please call Result.next()");
        Validate.isTrue(lastFetchColumnIdx >= 0, "Please call Result.getXXX()");
        Validate.isTrue(lastFetchRowIdx >= 0 && lastFetchRowIdx < lastFetchBlock.rowCnt(), "Please call Result.next()");
        return lastFetchBlock.getColumnByPosition(lastFetchColumnIdx).value(lastFetchRowIdx) == null;
    }

    @Override
    public int getHoldability() throws SQLException {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public Statement getStatement() throws SQLException {
        return statement;
    }

    @Override
    public int findColumn(String name) throws SQLException {
        LOG.trace("find column: {}", name);
        return header.getPositionByName(name);
    }

    @Override
    public boolean next() throws SQLException {
        boolean isBeforeFirst = isBeforeFirst();
        LOG.trace("check status[before]: is_before_first: {}, is_first: {}, is_after_last: {}", isBeforeFirst, isFirst, isAfterLast);

        boolean hasNext = ++currentRowNum < currentBlock.rowCnt() || (currentRowNum = 0) < (currentBlock = fetchBlock()).rowCnt();

        isFirst = isBeforeFirst && hasNext;
        isAfterLast = !hasNext;
        LOG.trace("check status[after]: has_next: {}, is_before_first: {}, is_first: {}, is_after_last: {}", hasNext, isBeforeFirst(), isFirst, isAfterLast);

        return hasNext;
    }

    @Override
    public void close() throws SQLException {
        // TODO check if query responses are completed
        //  1. if completed, just set isClosed = true
        //  2. if not, cancel query and consume the rest responses
        LOG.debug("close ResultSet");
        this.isClosed = true;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.isClosed;
    }

    @Override
    public Logger logger() {
        return ClickHouseResultSet.LOG;
    }

    private Block fetchBlock() throws SQLException {
        while (dataResponses.hasNext()) {
            LOG.trace("fetch next DataResponse");
            DataResponse next = dataResponses.next();
            if (next.block().rowCnt() > 0) {
                return next.block();
            }
        }
        LOG.debug("no more DataResponse, return empty Block");
        return new Block();
    }
}
