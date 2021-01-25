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

package com.github.housepower.jdbc.wrapper;

import com.github.housepower.log.Logging;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

public interface SQLResultSet extends ResultSet, SQLWrapper, Logging {

    @Override
    default boolean next() throws SQLException {
        logger().debug("invoke unimplemented method #next()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void close() throws SQLException {
        logger().debug("invoke unimplemented method #close()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean wasNull() throws SQLException {
        logger().debug("invoke unimplemented method #wasNull()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getString(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getString(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean getBoolean(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getBoolean(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default byte getByte(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getByte(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default short getShort(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getShort(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getInt(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getInt(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default long getLong(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getLong(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default float getFloat(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getFloat(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default double getDouble(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getDouble(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        logger().debug("invoke unimplemented method #getBigDecimal(int columnIndex, int scale)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default byte[] getBytes(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getBytes(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Date getDate(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getDate(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Time getTime(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getTime(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Timestamp getTimestamp(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getTimestamp(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default InputStream getAsciiStream(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getAsciiStream(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default InputStream getUnicodeStream(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getUnicodeStream(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default InputStream getBinaryStream(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getBinaryStream(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getString(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getString(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean getBoolean(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getBoolean(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default byte getByte(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getByte(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default short getShort(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getShort(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getInt(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getInt(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default long getLong(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getLong(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default float getFloat(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getFloat(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default double getDouble(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getDouble(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        logger().debug("invoke unimplemented method #getBigDecimal(String columnLabel, int scale)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default byte[] getBytes(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getBytes(String columnLabel");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Date getDate(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getDate(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Time getTime(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getTime(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Timestamp getTimestamp(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getTimestamp(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default InputStream getAsciiStream(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getAsciiStream(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default InputStream getUnicodeStream(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getUnicodeStream(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default InputStream getBinaryStream(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getBinaryStream(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default SQLWarning getWarnings() throws SQLException {
        logger().debug("invoke unimplemented method #getWarnings()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void clearWarnings() throws SQLException {
        logger().debug("invoke unimplemented method #clearWarnings()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getCursorName() throws SQLException {
        logger().debug("invoke unimplemented method #getCursorName()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSetMetaData getMetaData() throws SQLException {
        logger().debug("invoke unimplemented method #getMetaData()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Object getObject(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getObject(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Object getObject(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getObject(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int findColumn(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #findColumn(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Reader getCharacterStream(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getCharacterStream(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Reader getCharacterStream(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getCharacterStream(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getBigDecimal(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getBigDecimal(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isBeforeFirst() throws SQLException {
        logger().debug("invoke unimplemented method #isBeforeFirst()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isAfterLast() throws SQLException {
        logger().debug("invoke unimplemented method #isAfterLast()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isFirst() throws SQLException {
        logger().debug("invoke unimplemented method #isFirst()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isLast() throws SQLException {
        logger().debug("invoke unimplemented method #isLast()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void beforeFirst() throws SQLException {
        logger().debug("invoke unimplemented method #beforeFirst()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void afterLast() throws SQLException {
        logger().debug("invoke unimplemented method #afterLast()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean first() throws SQLException {
        logger().debug("invoke unimplemented method #first()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean last() throws SQLException {
        logger().debug("invoke unimplemented method #last()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getRow() throws SQLException {
        logger().debug("invoke unimplemented method #getRow()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean absolute(int row) throws SQLException {
        logger().debug("invoke unimplemented method #absolute(int row)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean relative(int rows) throws SQLException {
        logger().debug("invoke unimplemented method #relative(int rows)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean previous() throws SQLException {
        logger().debug("invoke unimplemented method #previous()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setFetchDirection(int direction) throws SQLException {
        logger().debug("invoke unimplemented method #setFetchDirection(int direction)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getFetchDirection() throws SQLException {
        logger().debug("invoke unimplemented method #getFetchDirection()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setFetchSize(int rows) throws SQLException {
        logger().debug("invoke unimplemented method #setFetchSize(int rows)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getFetchSize() throws SQLException {
        logger().debug("invoke unimplemented method #getFetchSize()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getType() throws SQLException {
        logger().debug("invoke unimplemented method #getType()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getConcurrency() throws SQLException {
        logger().debug("invoke unimplemented method #getConcurrency()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean rowUpdated() throws SQLException {
        logger().debug("invoke unimplemented method #rowUpdated()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean rowInserted() throws SQLException {
        logger().debug("invoke unimplemented method #rowInserted()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean rowDeleted() throws SQLException {
        logger().debug("invoke unimplemented method #rowDeleted()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateNull(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #updateNull(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBoolean(int columnIndex, boolean x) throws SQLException {
        logger().debug("invoke unimplemented method #updateBoolean(int columnIndex, boolean x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateByte(int columnIndex, byte x) throws SQLException {
        logger().debug("invoke unimplemented method #updateByte(int columnIndex, byte x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateShort(int columnIndex, short x) throws SQLException {
        logger().debug("invoke unimplemented method #updateShort(int columnIndex, short x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateInt(int columnIndex, int x) throws SQLException {
        logger().debug("invoke unimplemented method #updateInt(int columnIndex, int x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateLong(int columnIndex, long x) throws SQLException {
        logger().debug("invoke unimplemented method #updateLong(int columnIndex, long x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateFloat(int columnIndex, float x) throws SQLException {
        logger().debug("invoke unimplemented method #updateFloat(int columnIndex, float x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateDouble(int columnIndex, double x) throws SQLException {
        logger().debug("invoke unimplemented method #updateDouble(int columnIndex, double x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        logger().debug("invoke unimplemented method #updateBigDecimal(int columnIndex, BigDecimal x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateString(int columnIndex, String x) throws SQLException {
        logger().debug("invoke unimplemented method #updateString(int columnIndex, String x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBytes(int columnIndex, byte[] x) throws SQLException {
        logger().debug("invoke unimplemented method #updateBytes(int columnIndex, byte[] x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateDate(int columnIndex, Date x) throws SQLException {
        logger().debug("invoke unimplemented method #updateDate(int columnIndex, Date x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateTime(int columnIndex, Time x) throws SQLException {
        logger().debug("invoke unimplemented method #updateTime(int columnIndex, Time x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        logger().debug("invoke unimplemented method #updateTimestamp(int columnIndex, Timestamp x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        logger().debug("invoke unimplemented method #updateAsciiStream(int columnIndex, InputStream x, int length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        logger().debug("invoke unimplemented method #updateBinaryStream(int columnIndex, InputStream x, int length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        logger().debug("invoke unimplemented method #updateCharacterStream(int columnIndex, Reader x, int length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        logger().debug("invoke unimplemented method #updateObject(int columnIndex, Object x, int scaleOrLength)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateObject(int columnIndex, Object x) throws SQLException {
        logger().debug("invoke unimplemented method #updateObject(int columnIndex, Object x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateNull(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #updateNull(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBoolean(String columnLabel, boolean x) throws SQLException {
        logger().debug("invoke unimplemented method #updateBoolean(String columnLabel, boolean x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateByte(String columnLabel, byte x) throws SQLException {
        logger().debug("invoke unimplemented method #updateByte(String columnLabel, byte x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateShort(String columnLabel, short x) throws SQLException {
        logger().debug("invoke unimplemented method #updateShort(String columnLabel, short x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateInt(String columnLabel, int x) throws SQLException {
        logger().debug("invoke unimplemented method #updateInt(String columnLabel, int x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateLong(String columnLabel, long x) throws SQLException {
        logger().debug("invoke unimplemented method #updateLong(String columnLabel, long x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateFloat(String columnLabel, float x) throws SQLException {
        logger().debug("invoke unimplemented method #updateFloat(String columnLabel, float x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateDouble(String columnLabel, double x) throws SQLException {
        logger().debug("invoke unimplemented method #updateDouble(String columnLabel, double x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        logger().debug("invoke unimplemented method #updateBigDecimal(String columnLabel, BigDecimal x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateString(String columnLabel, String x) throws SQLException {
        logger().debug("invoke unimplemented method #updateString(String columnLabel, String x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBytes(String columnLabel, byte[] x) throws SQLException {
        logger().debug("invoke unimplemented method #updateBytes(String columnLabel, byte[] x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateDate(String columnLabel, Date x) throws SQLException {
        logger().debug("invoke unimplemented method #updateDate(String columnLabel, Date x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateTime(String columnLabel, Time x) throws SQLException {
        logger().debug("invoke unimplemented method #updateTime(String columnLabel, Time x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        logger().debug("invoke unimplemented method #updateTimestamp(String columnLabel, Timestamp x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        logger().debug("invoke unimplemented method #updateAsciiStream(String columnLabel, InputStream x, int length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        logger().debug("invoke unimplemented method #updateBinaryStream(String columnLabel, InputStream x, int length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        logger().debug("invoke unimplemented method #updateCharacterStream(String columnLabel, Reader reader, int length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        logger().debug("invoke unimplemented method #updateObject(String columnLabel, Object x, int scaleOrLength)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateObject(String columnLabel, Object x) throws SQLException {
        logger().debug("invoke unimplemented method #updateObject(String columnLabel, Object x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void insertRow() throws SQLException {
        logger().debug("invoke unimplemented method #insertRow()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateRow() throws SQLException {
        logger().debug("invoke unimplemented method #updateRow()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void deleteRow() throws SQLException {
        logger().debug("invoke unimplemented method #deleteRow()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void refreshRow() throws SQLException {
        logger().debug("invoke unimplemented method #refreshRow()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void cancelRowUpdates() throws SQLException {
        logger().debug("invoke unimplemented method #cancelRowUpdates()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void moveToInsertRow() throws SQLException {
        logger().debug("invoke unimplemented method #moveToInsertRow()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void moveToCurrentRow() throws SQLException {
        logger().debug("invoke unimplemented method #moveToCurrentRow()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Statement getStatement() throws SQLException {
        logger().debug("invoke unimplemented method #getStatement()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        logger().debug("invoke unimplemented method #getObject(int columnIndex, Map<String, Class<?>> map)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Ref getRef(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getRef(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Blob getBlob(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getBlob(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Clob getClob(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getClob(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Array getArray(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getArray(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        logger().debug("invoke unimplemented method #getObject(String columnLabel, Map<String, Class<?>> map)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Ref getRef(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getRef(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Blob getBlob(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getBlob(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Clob getClob(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getClob(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Array getArray(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getArray(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Date getDate(int columnIndex, Calendar cal) throws SQLException {
        logger().debug("invoke unimplemented method #getDate(int columnIndex, Calendar cal)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Date getDate(String columnLabel, Calendar cal) throws SQLException {
        logger().debug("invoke unimplemented method #getDate(String columnLabel, Calendar cal)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Time getTime(int columnIndex, Calendar cal) throws SQLException {
        logger().debug("invoke unimplemented method #Time getTime(int columnIndex, Calendar cal)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Time getTime(String columnLabel, Calendar cal) throws SQLException {
        logger().debug("invoke unimplemented method #getTime(String columnLabel, Calendar cal)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        logger().debug("invoke unimplemented method #getTimestamp(int columnIndex, Calendar cal)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        logger().debug("invoke unimplemented method #getTimestamp(String columnLabel, Calendar cal)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default URL getURL(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getURL(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default URL getURL(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getURL(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateRef(int columnIndex, Ref x) throws SQLException {
        logger().debug("invoke unimplemented method #updateRef(int columnIndex, Ref x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateRef(String columnLabel, Ref x) throws SQLException {
        logger().debug("invoke unimplemented method #updateRef(String columnLabel, Ref x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBlob(int columnIndex, Blob x) throws SQLException {
        logger().debug("invoke unimplemented method #updateBlob(int columnIndex, Blob x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBlob(String columnLabel, Blob x) throws SQLException {
        logger().debug("invoke unimplemented method #updateBlob(String columnLabel, Blob x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateClob(int columnIndex, Clob x) throws SQLException {
        logger().debug("invoke unimplemented method #updateClob(int columnIndex, Clob x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateClob(String columnLabel, Clob x) throws SQLException {
        logger().debug("invoke unimplemented method #updateClob(String columnLabel, Clob x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateArray(int columnIndex, Array x) throws SQLException {
        logger().debug("invoke unimplemented method #updateArray(int columnIndex, Array x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateArray(String columnLabel, Array x) throws SQLException {
        logger().debug("invoke unimplemented method #updateArray(String columnLabel, Array x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default RowId getRowId(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getRowId(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default RowId getRowId(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getRowId(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateRowId(int columnIndex, RowId x) throws SQLException {
        logger().debug("invoke unimplemented method #updateRowId(int columnIndex, RowId x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateRowId(String columnLabel, RowId x) throws SQLException {
        logger().debug("invoke unimplemented method #updateRowId(String columnLabel, RowId x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getHoldability() throws SQLException {
        logger().debug("invoke unimplemented method #getHoldability()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isClosed() throws SQLException {
        logger().debug("invoke unimplemented method #isClosed()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateNString(int columnIndex, String nString) throws SQLException {
        logger().debug("invoke unimplemented method #updateNString(int columnIndex, String nString)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateNString(String columnLabel, String nString) throws SQLException {
        logger().debug("invoke unimplemented method #updateNString(String columnLabel, String nString)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        logger().debug("invoke unimplemented method #updateNClob(int columnIndex, NClob nClob)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        logger().debug("invoke unimplemented method #updateNClob(String columnLabel, NClob nClob)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default NClob getNClob(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getNClob(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default NClob getNClob(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getNClob(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default SQLXML getSQLXML(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getSQLXML(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default SQLXML getSQLXML(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getSQLXML(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        logger().debug("invoke unimplemented method #updateSQLXML(int columnIndex, SQLXML xmlObject)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        logger().debug("invoke unimplemented method #updateSQLXML(String columnLabel, SQLXML xmlObject)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getNString(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getNString(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getNString(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getNString(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Reader getNCharacterStream(int columnIndex) throws SQLException {
        logger().debug("invoke unimplemented method #getNCharacterStream(int columnIndex)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Reader getNCharacterStream(String columnLabel) throws SQLException {
        logger().debug("invoke unimplemented method #getNCharacterStream(String columnLabel)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        logger().debug("invoke unimplemented method #updateNCharacterStream(int columnIndex, Reader x, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        logger().debug("invoke unimplemented method #updateNCharacterStream(String columnLabel, Reader reader, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        logger().debug("invoke unimplemented method #updateAsciiStream(int columnIndex, InputStream x, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        logger().debug("invoke unimplemented method #updateBinaryStream(int columnIndex, InputStream x, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        logger().debug("invoke unimplemented method #updateCharacterStream(int columnIndex, Reader x, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        logger().debug("invoke unimplemented method #updateAsciiStream(String columnLabel, InputStream x, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        logger().debug("invoke unimplemented method #updateBinaryStream(String columnLabel, InputStream x, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        logger().debug("invoke unimplemented method #updateCharacterStream(String columnLabel, Reader reader, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        logger().debug("invoke unimplemented method #updateBlob(int columnIndex, InputStream inputStream, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        logger().debug("invoke unimplemented method #updateBlob(String columnLabel, InputStream inputStream, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        logger().debug("invoke unimplemented method #updateClob(int columnIndex, Reader reader, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        logger().debug("invoke unimplemented method #updateClob(String columnLabel, Reader reader, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        logger().debug("invoke unimplemented method #updateNClob(int columnIndex, Reader reader, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        logger().debug("invoke unimplemented method #updateNClob(String columnLabel, Reader reader, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        logger().debug("invoke unimplemented method #updateNCharacterStream(int columnIndex, Reader x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        logger().debug("invoke unimplemented method #updateNCharacterStream(String columnLabel, Reader reader)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        logger().debug("invoke unimplemented method #updateAsciiStream(int columnIndex, InputStream x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        logger().debug("invoke unimplemented method #updateBinaryStream(int columnIndex, InputStream x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        logger().debug("invoke unimplemented method #updateCharacterStream(int columnIndex, Reader x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        logger().debug("invoke unimplemented method #updateAsciiStream(String columnLabel, InputStream x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        logger().debug("invoke unimplemented method #updateBinaryStream(String columnLabel, InputStream x)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        logger().debug("invoke unimplemented method #updateCharacterStream(String columnLabel, Reader reader)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        logger().debug("invoke unimplemented method #updateBlob(int columnIndex, InputStream inputStream)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        logger().debug("invoke unimplemented method #updateBlob(String columnLabel, InputStream inputStream)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateClob(int columnIndex, Reader reader) throws SQLException {
        logger().debug("invoke unimplemented method #updateClob(int columnIndex, Reader reader)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateClob(String columnLabel, Reader reader) throws SQLException {
        logger().debug("invoke unimplemented method #updateClob(String columnLabel, Reader reader)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateNClob(int columnIndex, Reader reader) throws SQLException {
        logger().debug("invoke unimplemented method #updateNClob(int columnIndex, Reader reader)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void updateNClob(String columnLabel, Reader reader) throws SQLException {
        logger().debug("invoke unimplemented method #updateNClob(String columnLabel, Reader reader)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        logger().debug("invoke unimplemented method #getObject(int columnIndex, Class<T> type)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        logger().debug("invoke unimplemented method #getObject(String columnLabel, Class<T> type)");
        throw new SQLFeatureNotSupportedException();
    }
}
