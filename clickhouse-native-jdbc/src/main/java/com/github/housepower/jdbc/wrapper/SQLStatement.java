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

public interface SQLStatement extends Statement, SQLWrapper, Logging {

    @Override
    default ResultSet executeQuery(String sql) throws SQLException {
        logger().debug("invoke unimplemented method #executeQuery(String sql)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int executeUpdate(String sql) throws SQLException {
        logger().debug("invoke unimplemented method #executeUpdate(String sql)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void close() throws SQLException {
        logger().debug("invoke unimplemented method #close()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxFieldSize() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxFieldSize()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setMaxFieldSize(int max) throws SQLException {
        logger().debug("invoke unimplemented method #setMaxFieldSize(int max)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxRows() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxRows()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setMaxRows(int max) throws SQLException {
        logger().debug("invoke unimplemented method #setMaxRows(int max)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setEscapeProcessing(boolean enable) throws SQLException {
        logger().debug("invoke unimplemented method #setEscapeProcessing(boolean enable)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getQueryTimeout() throws SQLException {
        logger().debug("invoke unimplemented method #getQueryTimeout()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setQueryTimeout(int seconds) throws SQLException {
        logger().debug("invoke unimplemented method #setQueryTimeout(int seconds)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void cancel() throws SQLException {
        logger().debug("invoke unimplemented method #cancel()");
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
    default void setCursorName(String name) throws SQLException {
        logger().debug("invoke unimplemented method #setCursorName(String name)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean execute(String sql) throws SQLException {
        logger().debug("invoke unimplemented method #execute(String sql)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getResultSet() throws SQLException {
        logger().debug("invoke unimplemented method #getResultSet()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getUpdateCount() throws SQLException {
        logger().debug("invoke unimplemented method #getUpdateCount()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean getMoreResults() throws SQLException {
        logger().debug("invoke unimplemented method #getMoreResults()");
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
    default int getResultSetConcurrency() throws SQLException {
        logger().debug("invoke unimplemented method #getResultSetConcurrency()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getResultSetType() throws SQLException {
        logger().debug("invoke unimplemented method #getResultSetType()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void addBatch(String sql) throws SQLException {
        logger().debug("invoke unimplemented method #addBatch(String sql)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void clearBatch() throws SQLException {
        logger().debug("invoke unimplemented method #clearBatch()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int[] executeBatch() throws SQLException {
        logger().debug("invoke unimplemented method #executeBatch()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Connection getConnection() throws SQLException {
        logger().debug("invoke unimplemented method #getConnection()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean getMoreResults(int current) throws SQLException {
        logger().debug("invoke unimplemented method #getMoreResults(int current)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getGeneratedKeys() throws SQLException {
        logger().debug("invoke unimplemented method #getGeneratedKeys()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        logger().debug("invoke unimplemented method #executeUpdate(String sql, int autoGeneratedKeys)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        logger().debug("invoke unimplemented method #executeUpdate(String sql, int[] columnIndexes)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int executeUpdate(String sql, String[] columnNames) throws SQLException {
        logger().debug("invoke unimplemented method #executeUpdate(String sql, String[] columnNames)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        logger().debug("invoke unimplemented method #execute(String sql, int autoGeneratedKeys)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean execute(String sql, int[] columnIndexes) throws SQLException {
        logger().debug("invoke unimplemented method #execute(String sql, int[] columnIndexes)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean execute(String sql, String[] columnNames) throws SQLException {
        logger().debug("invoke unimplemented method #execute(String sql, String[] columnNames)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getResultSetHoldability() throws SQLException {
        logger().debug("invoke unimplemented method #getResultSetHoldability()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isClosed() throws SQLException {
        logger().debug("invoke unimplemented method #isClosed()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setPoolable(boolean poolable) throws SQLException {
        logger().debug("invoke unimplemented method #setPoolable(boolean poolable)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isPoolable() throws SQLException {
        logger().debug("invoke unimplemented method #isPoolable()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void closeOnCompletion() throws SQLException {
        logger().debug("invoke unimplemented method #closeOnCompletion()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isCloseOnCompletion() throws SQLException {
        logger().debug("invoke unimplemented method #isCloseOnCompletion()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default long getLargeUpdateCount() throws SQLException {
        logger().debug("invoke unimplemented method #getLargeUpdateCount()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void setLargeMaxRows(long max) throws SQLException {
        logger().debug("invoke unimplemented method #setLargeMaxRows(long max)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default long getLargeMaxRows() throws SQLException {
        logger().debug("invoke unimplemented method #getLargeMaxRows()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default long[] executeLargeBatch() throws SQLException {
        logger().debug("invoke unimplemented method #executeLargeBatch()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default long executeLargeUpdate(String sql) throws SQLException {
        logger().debug("invoke unimplemented method #executeLargeUpdate(String sql)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        logger().debug("invoke unimplemented method #executeLargeUpdate(String sql, int autoGeneratedKeys)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        logger().debug("invoke unimplemented method #executeLargeUpdate(String sql, int[] columnIndexes)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        logger().debug("invoke unimplemented method #executeLargeUpdate(String sql, String[] columnNames)");
        throw new SQLFeatureNotSupportedException();
    }
}
