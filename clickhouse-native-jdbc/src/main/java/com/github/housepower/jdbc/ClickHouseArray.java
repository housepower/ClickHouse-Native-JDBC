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

import com.github.housepower.data.IDataType;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.jdbc.wrapper.SQLArray;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.function.BiFunction;

public class ClickHouseArray implements SQLArray {

    private static final Logger LOG = LoggerFactory.getLogger(ClickHouseArray.class);

    private final IDataType<?, ?> elementDataType;
    private final Object[] elements;

    public ClickHouseArray(IDataType<?, ?> elementDataType, Object[] elements) {
        this.elementDataType = elementDataType;
        this.elements = elements;
    }

    @Override
    public String getBaseTypeName() throws SQLException {
        return elementDataType.name();
    }

    @Override
    public int getBaseType() {
        return elementDataType.sqlTypeId();
    }

    @Override
    public void free() throws SQLException {
    }

    @Override
    public Object[] getArray() throws SQLException {
        return elements;
    }

    @Override
    public Logger logger() {
        return ClickHouseArray.LOG;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (Object item : elements) {
            // TODO format by itemDataType
            joiner.add(String.valueOf(item));
        }
        return joiner.toString();
    }

    public ClickHouseArray slice(int offset, int length) {
        Object[] result = new Object[length];
        if (length >= 0) System.arraycopy(elements, offset, result, 0, length);
        return new ClickHouseArray(elementDataType, result);
    }

    public ClickHouseArray mapElements(BiFunction<IDataType<?, ?>, Object, Object> mapFunc) {
        Object[] mapped = Arrays.stream(elements).map(elem -> mapFunc.apply(elementDataType, elem)).toArray();
        return new ClickHouseArray(elementDataType, mapped);
    }
}
