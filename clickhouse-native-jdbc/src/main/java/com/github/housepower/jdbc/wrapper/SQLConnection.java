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

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public interface SQLConnection extends Connection, SQLWrapper, Logging {

    @Override
    default void setClientInfo(Properties properties) throws SQLClientInfoException {
        logger().debug("invoke unimplemented method #setClientInfo(Properties properties)");
        throw new SQLClientInfoException();
    }

    @Override
    default void setClientInfo(String name, String value) throws SQLClientInfoException {
        logger().debug("invoke unimplemented method #setClientInfo(String name, String value)");
        throw new SQLClientInfoException();
    }

    @Override
    default Statement createStatement() throws SQLException {
        logger().debug("invoke unimplemented method #createStatement()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default PreparedStatement prepareStatement(String sql) throws SQLException {
        logger().debug("invoke unimplemented method #prepareStatement(String sql)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default CallableStatement prepareCall(String sql) throws SQLException {
        logger().debug("invoke unimplemented method #prepareCall(String sql)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String nativeSQL(String sql) throws SQLException {
        logger().debug("invoke unimplemented method #nativeSQL(String sql)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setAutoCommit(boolean autoCommit) throws SQLException {
        logger().debug("invoke unimplemented method #setAutoCommit(boolean autoCommit)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean getAutoCommit() throws SQLException {
        logger().debug("invoke unimplemented method #getAutoCommit()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void commit() throws SQLException {
        logger().debug("invoke unimplemented method #commit()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void rollback() throws SQLException {
        logger().debug("invoke unimplemented method #rollback()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void close() throws SQLException {
        logger().debug("invoke unimplemented method #close()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isClosed() throws SQLException {
        logger().debug("invoke unimplemented method #isClosed()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default DatabaseMetaData getMetaData() throws SQLException {
        logger().debug("invoke unimplemented method #getMetaData()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setReadOnly(boolean readOnly) throws SQLException {
        logger().debug("invoke unimplemented method #setReadOnly(boolean readOnly)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isReadOnly() throws SQLException {
        logger().debug("invoke unimplemented method #isReadOnly()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setCatalog(String catalog) throws SQLException {
        logger().debug("invoke unimplemented method #setCatalog(String catalog)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getCatalog() throws SQLException {
        logger().debug("invoke unimplemented method #getCatalog()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setTransactionIsolation(int level) throws SQLException {
        logger().debug("invoke unimplemented method #setTransactionIsolation(int level)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getTransactionIsolation() throws SQLException {
        logger().debug("invoke unimplemented method #getTransactionIsolation()");
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
    default Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        logger().debug("invoke unimplemented method #createStatement(int resultSetType, int resultSetConcurrency)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        logger().debug("invoke unimplemented method #prepareStatement(String sql, int resultSetType, int resultSetConcurrency)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        logger().debug("invoke unimplemented method #prepareCall(String sql, int resultSetType, int resultSetConcurrency)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Map<String, Class<?>> getTypeMap() throws SQLException {
        logger().debug("invoke unimplemented method #getTypeMap()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        logger().debug("invoke unimplemented method #setTypeMap(Map<String, Class<?>> map)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setHoldability(int holdability) throws SQLException {
        logger().debug("invoke unimplemented method #setHoldability(int holdability)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getHoldability() throws SQLException {
        logger().debug("invoke unimplemented method #getHoldability()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Savepoint setSavepoint() throws SQLException {
        logger().debug("invoke unimplemented method #setSavepoint()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Savepoint setSavepoint(String name) throws SQLException {
        logger().debug("invoke unimplemented method #setSavepoint(String name)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void rollback(Savepoint savepoint) throws SQLException {
        logger().debug("invoke unimplemented method #rollback(Savepoint savepoint)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void releaseSavepoint(Savepoint savepoint) throws SQLException {
        logger().debug("invoke unimplemented method #releaseSavepoint(Savepoint savepoint)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        logger().debug("invoke unimplemented method #createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        logger().debug("invoke unimplemented method #prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        logger().debug("invoke unimplemented method #prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        logger().debug("invoke unimplemented method #prepareStatement(String sql, int autoGeneratedKeys)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        logger().debug("invoke unimplemented method #prepareStatement(String sql, int[] columnIndexes)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        logger().debug("invoke unimplemented method #prepareStatement(String sql, String[] columnNames)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Clob createClob() throws SQLException {
        logger().debug("invoke unimplemented method #createClob()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Blob createBlob() throws SQLException {
        logger().debug("invoke unimplemented method #createBlob()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default NClob createNClob() throws SQLException {
        logger().debug("invoke unimplemented method #createNClob()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default SQLXML createSQLXML() throws SQLException {
        logger().debug("invoke unimplemented method #createSQLXML()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isValid(int timeout) throws SQLException {
        logger().debug("invoke unimplemented method #isValid(int timeout)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getClientInfo(String name) throws SQLException {
        logger().debug("invoke unimplemented method #getClientInfo(String name)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Properties getClientInfo() throws SQLException {
        logger().debug("invoke unimplemented method #getClientInfo()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        logger().debug("invoke unimplemented method #createArrayOf(String typeName, Object[] elements)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        logger().debug("invoke unimplemented method #createStruct(String typeName, Object[] attributes)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setSchema(String schema) throws SQLException {
        logger().debug("invoke unimplemented method #setSchema(String schema)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getSchema() throws SQLException {
        logger().debug("invoke unimplemented method #getSchema()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void abort(Executor executor) throws SQLException {
        logger().debug("invoke unimplemented method #abort(Executor executor)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        logger().debug("invoke unimplemented method #setNetworkTimeout(Executor executor, int milliseconds)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getNetworkTimeout() throws SQLException {
        logger().debug("invoke unimplemented method #getNetworkTimeout()");
        throw new SQLFeatureNotSupportedException();
    }
}
