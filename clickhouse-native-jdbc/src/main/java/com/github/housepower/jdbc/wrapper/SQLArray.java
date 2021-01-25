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

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;

public interface SQLArray extends Array, Logging {

    @Override
    default String getBaseTypeName() throws SQLException {
        logger().debug("invoke unimplemented method #getBaseTypeName()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getBaseType() throws SQLException {
        logger().debug("invoke unimplemented method #getBaseType()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Object getArray() throws SQLException {
        logger().debug("invoke unimplemented method #getArray()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Object getArray(Map<String, Class<?>> map) throws SQLException {
        logger().debug("invoke unimplemented method #getArray(Map<String, Class<?>> map)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Object getArray(long index, int count) throws SQLException {
        logger().debug("invoke unimplemented method #getArray(long index, int count)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
        logger().debug("invoke unimplemented method #getArray(long index, int count, Map<String, Class<?>> map)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getResultSet() throws SQLException {
        logger().debug("invoke unimplemented method #getResultSet()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
        logger().debug("invoke unimplemented method #getResultSet(Map<String, Class<?>> map)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getResultSet(long index, int count) throws SQLException {
        logger().debug("invoke unimplemented method #getResultSet(long index, int count)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
        logger().debug("invoke unimplemented method #getResultSet(long index, int count, Map<String, Class<?>> map)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default void free() throws SQLException {
        logger().debug("invoke unimplemented method #free()");
        throw new SQLFeatureNotSupportedException();
    }
}
