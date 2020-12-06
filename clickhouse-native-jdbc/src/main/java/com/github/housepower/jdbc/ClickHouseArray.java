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

import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.log.Logger;
import com.github.housepower.jdbc.log.LoggerFactory;
import com.github.housepower.jdbc.wrapper.SQLArray;

import java.sql.SQLException;

public class ClickHouseArray implements SQLArray {

    private static final Logger LOG = LoggerFactory.getLogger(ClickHouseArray.class);

    private final Object[] data;
    private final IDataType dataType;

    public ClickHouseArray(IDataType dataType, Object[] data) {
        this.dataType = dataType;
        this.data = data;
    }

    @Override
    public String getBaseTypeName() throws SQLException {
        return dataType.name();
    }

    @Override
    public int getBaseType() throws SQLException {
        return dataType.sqlTypeId();
    }

    @Override
    public void free() throws SQLException {
    }

    @Override
    public Object getArray() throws SQLException {
        return data;
    }

    @Override
    public Logger logger() {
        return ClickHouseArray.LOG;
    }

    public ClickHouseArray slice(int offset, int length) {
        Object[] result = new Object[length];
        if (length >= 0) System.arraycopy(data, offset, result, 0, length);
        return new ClickHouseArray(dataType, result);
    }
}
