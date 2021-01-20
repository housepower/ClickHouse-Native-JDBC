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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public interface SQLResultSetMetaData extends ResultSetMetaData, SQLWrapper, Logging {
    @Override
    default int getColumnCount() throws SQLException {
        logger().debug("invoke unimplemented method #getColumnCount()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isAutoIncrement(int column) throws SQLException {
        logger().debug("invoke unimplemented method #isAutoIncrement(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isCaseSensitive(int column) throws SQLException {
        logger().debug("invoke unimplemented method #isCaseSensitive(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isSearchable(int column) throws SQLException {
        logger().debug("invoke unimplemented method #isSearchable(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isCurrency(int column) throws SQLException {
        logger().debug("invoke unimplemented method #isCurrency(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int isNullable(int column) throws SQLException {
        logger().debug("invoke unimplemented method #isNullable(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isSigned(int column) throws SQLException {
        logger().debug("invoke unimplemented method #isSigned(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getColumnDisplaySize(int column) throws SQLException {
        logger().debug("invoke unimplemented method #getColumnDisplaySize(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getColumnLabel(int column) throws SQLException {
        logger().debug("invoke unimplemented method #getColumnLabel(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getColumnName(int column) throws SQLException {
        logger().debug("invoke unimplemented method #getColumnName(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getSchemaName(int column) throws SQLException {
        logger().debug("invoke unimplemented method #getSchemaName(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getPrecision(int column) throws SQLException {
        logger().debug("invoke unimplemented method #getPrecision(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getScale(int column) throws SQLException {
        logger().debug("invoke unimplemented method #getScale(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getTableName(int column) throws SQLException {
        logger().debug("invoke unimplemented method #getTableName(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getCatalogName(int column) throws SQLException {
        logger().debug("invoke unimplemented method #getCatalogName(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getColumnType(int column) throws SQLException {
        logger().debug("invoke unimplemented method #getColumnType(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getColumnTypeName(int column) throws SQLException {
        logger().debug("invoke unimplemented method #getColumnTypeName(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isReadOnly(int column) throws SQLException {
        logger().debug("invoke unimplemented method #isReadOnly(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isWritable(int column) throws SQLException {
        logger().debug("invoke unimplemented method #isWritable(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isDefinitelyWritable(int column) throws SQLException {
        logger().debug("invoke unimplemented method #isDefinitelyWritable(int column)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getColumnClassName(int column) throws SQLException {
        logger().debug("invoke unimplemented method #getColumnClassName(int column)");
        throw new SQLFeatureNotSupportedException();
    }
}
