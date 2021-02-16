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

import com.github.housepower.data.Block;
import com.github.housepower.data.type.complex.DataTypeNullable;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.jdbc.wrapper.SQLResultSetMetaData;
import com.github.housepower.settings.ClickHouseDefines;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ClickHouseResultSetMetaData implements SQLResultSetMetaData {

    private static final Logger LOG = LoggerFactory.getLogger(ClickHouseResultSetMetaData.class);

    private final Block header;
    private final String db;
    private final String table;

    public ClickHouseResultSetMetaData(Block header, String db, String table) {
        this.header = header;
        this.db = db;
        this.table = table;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return header.columnCnt();
    }

    @Override
    public int getColumnType(int index) throws SQLException {
        return header.getColumn(index - 1).type().sqlTypeId();
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return 80;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return header.getColumn(column - 1).type().name();
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return header.getColumn(column - 1).type().jdbcJavaType().getName();
    }

    @Override
    public String getColumnName(int index) throws SQLException {
        return getColumnLabel(index);
    }

    @Override
    public String getColumnLabel(int index) throws SQLException {
        return header.getColumn(index - 1).name();
    }

    @Override
    public int isNullable(int index) throws SQLException {
        return (header.getColumn(index - 1).type() instanceof DataTypeNullable) ?
            ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls;
    }

    @Override
    public boolean isSigned(int index) throws SQLException {
        return header.getColumn(index - 1).type().isSigned();
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return header.getColumn(column - 1).type().getPrecision();
    }

    @Override
    public int getScale(int column) throws SQLException {
        return header.getColumn(column - 1).type().getScale();
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return table;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return ClickHouseDefines.DEFAULT_CATALOG;
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return db;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isReadOnly(int index) throws SQLException {
        return true;
    }

    @Override
    public boolean isWritable(int index) throws SQLException {
        return false;
    }

    @Override
    public boolean isAutoIncrement(int index) throws SQLException {
        return false;
    }

    @Override
    public Logger logger() {
        return ClickHouseResultSetMetaData.LOG;
    }
}
