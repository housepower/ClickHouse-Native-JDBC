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

public interface SQLPreparedStatement extends SQLStatement, PreparedStatement, Logging {

    default ResultSet executeQuery() throws SQLException {
        logger().debug("invoke unimplemented method #executeQuery()");
        throw new SQLFeatureNotSupportedException();
    }

    default int executeUpdate() throws SQLException {
        logger().debug("invoke unimplemented method #executeUpdate()");
        throw new SQLFeatureNotSupportedException();
    }

    default void setNull(int parameterIndex, int sqlType) throws SQLException {
        logger().debug("invoke unimplemented method #setNull(int parameterIndex, int sqlType)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setBoolean(int parameterIndex, boolean x) throws SQLException {
        logger().debug("invoke unimplemented method #setBoolean(int parameterIndex, boolean x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setByte(int parameterIndex, byte x) throws SQLException {
        logger().debug("invoke unimplemented method #setByte(int parameterIndex, byte x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setShort(int parameterIndex, short x) throws SQLException {
        logger().debug("invoke unimplemented method #setShort(int parameterIndex, short x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setInt(int parameterIndex, int x) throws SQLException {
        logger().debug("invoke unimplemented method #setInt(int parameterIndex, int x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setLong(int parameterIndex, long x) throws SQLException {
        logger().debug("invoke unimplemented method #setLong(int parameterIndex, long x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setFloat(int parameterIndex, float x) throws SQLException {
        logger().debug("invoke unimplemented method #setFloat(int parameterIndex, float x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setDouble(int parameterIndex, double x) throws SQLException {
        logger().debug("invoke unimplemented method #setDouble(int parameterIndex, double x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        logger().debug("invoke unimplemented method #setBigDecimal(int parameterIndex, BigDecimal x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setString(int parameterIndex, String x) throws SQLException {
        logger().debug("invoke unimplemented method #setString(int parameterIndex, String x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setBytes(int parameterIndex, byte x[]) throws SQLException {
        logger().debug("invoke unimplemented method #setBytes(int parameterIndex, byte x[])");
        throw new SQLFeatureNotSupportedException();
    }

    default void setDate(int parameterIndex, java.sql.Date x) throws SQLException {
        logger().debug("invoke unimplemented method #setDate(int parameterIndex, java.sql.Date x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setTime(int parameterIndex, java.sql.Time x) throws SQLException {
        logger().debug("invoke unimplemented method #setTime(int parameterIndex, java.sql.Time x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setTimestamp(int parameterIndex, java.sql.Timestamp x) throws SQLException {
        logger().debug("invoke unimplemented method #setTimestamp(int parameterIndex, java.sql.Timestamp x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setAsciiStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
        logger().debug("invoke unimplemented method #setAsciiStream(int parameterIndex, java.io.InputStream x, int length)");
        throw new SQLFeatureNotSupportedException();
    }

    @Deprecated
    default void setUnicodeStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
        logger().debug("invoke unimplemented method #setUnicodeStream(int parameterIndex, java.io.InputStream x, int length)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setBinaryStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
        logger().debug("invoke unimplemented method #setBinaryStream(int parameterIndex, java.io.InputStream x, int length)");
        throw new SQLFeatureNotSupportedException();
    }


    default void clearParameters() throws SQLException {
        logger().debug("invoke unimplemented method #clearParameters()");
        throw new SQLFeatureNotSupportedException();
    }


    default void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        logger().debug("invoke unimplemented method #setObject(int parameterIndex, Object x, int targetSqlType)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setObject(int parameterIndex, Object x) throws SQLException {
        logger().debug("invoke unimplemented method #setObject(int parameterIndex, Object x)");
        throw new SQLFeatureNotSupportedException();
    }


    default boolean execute() throws SQLException {
        logger().debug("invoke unimplemented method #execute()");
        throw new SQLFeatureNotSupportedException();
    }

    //--------------------------JDBC 2.0-----------------------------

    default void addBatch() throws SQLException {
        logger().debug("invoke unimplemented method #addBatch()");
        throw new SQLFeatureNotSupportedException();
    }

    default void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        logger().debug("invoke unimplemented method #setCharacterStream(int parameterIndex, Reader reader, int length)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setRef(int parameterIndex, Ref x) throws SQLException {
        logger().debug("invoke unimplemented method #setRef(int parameterIndex, Ref x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setBlob(int parameterIndex, Blob x) throws SQLException {
        logger().debug("invoke unimplemented method #setBlob(int parameterIndex, Blob x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setClob(int parameterIndex, Clob x) throws SQLException {
        logger().debug("invoke unimplemented method #setClob(int parameterIndex, Clob x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setArray(int parameterIndex, Array x) throws SQLException {
        logger().debug("invoke unimplemented method #setArray(int parameterIndex, Array x)");
        throw new SQLFeatureNotSupportedException();
    }

    default ResultSetMetaData getMetaData() throws SQLException {
        logger().debug("invoke unimplemented method #getMetaData()");
        throw new SQLFeatureNotSupportedException();
    }


    default void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        logger().debug("invoke unimplemented method #setDate(int parameterIndex, Date x, Calendar cal)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        logger().debug("invoke unimplemented method #setTime(int parameterIndex, Time x, Calendar cal)");
        throw new SQLFeatureNotSupportedException();
    }


    default void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        logger().debug("invoke unimplemented method #setTimestamp(int parameterIndex, Timestamp x, Calendar cal)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        logger().debug("invoke unimplemented method #setNull(int parameterIndex, int sqlType, String typeName)");
        throw new SQLFeatureNotSupportedException();
    }

    //------------------------- JDBC 3.0 -----------------------------------

    default void setURL(int parameterIndex, URL x) throws SQLException {
        logger().debug("invoke unimplemented method #setURL(int parameterIndex, URL x)");
        throw new SQLFeatureNotSupportedException();
    }

    default ParameterMetaData getParameterMetaData() throws SQLException {
        logger().debug("invoke unimplemented method #getParameterMetaData()");
        throw new SQLFeatureNotSupportedException();
    }

    //------------------------- JDBC 4.0 -----------------------------------


    default void setRowId(int parameterIndex, RowId x) throws SQLException {
        logger().debug("invoke unimplemented method #setRowId(int parameterIndex, RowId x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setNString(int parameterIndex, String value) throws SQLException {
        logger().debug("invoke unimplemented method #setNString(int parameterIndex, String value)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        logger().debug("invoke unimplemented method #setNCharacterStream(int parameterIndex, Reader value, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setNClob(int parameterIndex, NClob value) throws SQLException {
        logger().debug("invoke unimplemented method #setNClob(int parameterIndex, NClob value)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        logger().debug("invoke unimplemented method #setClob(int parameterIndex, Reader reader, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        logger().debug("invoke unimplemented method #setBlob(int parameterIndex, InputStream inputStream, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        logger().debug("invoke unimplemented method #setNClob(int parameterIndex, Reader reader, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        logger().debug("invoke unimplemented method #setSQLXML(int parameterIndex, SQLXML xmlObject)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        logger().debug("invoke unimplemented method #setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setAsciiStream(int parameterIndex, java.io.InputStream x, long length) throws SQLException {
        logger().debug("invoke unimplemented method #setAsciiStream(int parameterIndex, java.io.InputStream x, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        logger().debug("invoke unimplemented method #setBinaryStream(int parameterIndex, InputStream x, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        logger().debug("invoke unimplemented method #setCharacterStream(int parameterIndex, Reader reader, long length)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        logger().debug("invoke unimplemented method #setAsciiStream(int parameterIndex, InputStream x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        logger().debug("invoke unimplemented method #setBinaryStream(int parameterIndex, InputStream x)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        logger().debug("invoke unimplemented method #setCharacterStream(int parameterIndex, Reader reader)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        logger().debug("invoke unimplemented method #setNCharacterStream(int parameterIndex, Reader value)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setClob(int parameterIndex, Reader reader) throws SQLException {
        logger().debug("invoke unimplemented method #setClob(int parameterIndex, Reader reader)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        logger().debug("invoke unimplemented method #setBlob(int parameterIndex, InputStream inputStream)");
        throw new SQLFeatureNotSupportedException();
    }

    default void setNClob(int parameterIndex, Reader reader) throws SQLException {
        logger().debug("invoke unimplemented method #setNClob(int parameterIndex, Reader reader)");
        throw new SQLFeatureNotSupportedException();
    }

    //------------------------- JDBC 4.2 -----------------------------------
    default void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        logger().debug("invoke unimplemented method #setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength)");
        throw new SQLFeatureNotSupportedException("setObject not implemented");
    }

    default void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
        logger().debug("invoke unimplemented method #setObject(int parameterIndex, Object x, SQLType targetSqlType)");
        throw new SQLFeatureNotSupportedException("setObject not implemented");
    }

    default long executeLargeUpdate() throws SQLException {
        logger().debug("invoke unimplemented method #executeLargeUpdate()");
        throw new UnsupportedOperationException("executeLargeUpdate not implemented");
    }
}
