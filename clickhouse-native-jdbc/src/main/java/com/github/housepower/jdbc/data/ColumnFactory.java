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

package com.github.housepower.jdbc.data;

import com.github.housepower.jdbc.data.type.complex.DataTypeArray;
import com.github.housepower.jdbc.data.type.complex.DataTypeNullable;
import com.github.housepower.jdbc.data.type.complex.DataTypeTuple;

import java.sql.Types;

public class ColumnFactory {

    public static IColumn createColumn(String name, IDataType type, Object[] values) {
        if (type.sqlTypeId() == Types.ARRAY) {
            return new ColumnArray(name, (DataTypeArray) type, values);
        } else if (type.nullable()) {
            return new ColumnNullable(name, (DataTypeNullable) type, values);
        } else if (type.sqlTypeId() == Types.STRUCT) {
            return new ColumnTuple(name, (DataTypeTuple) type, values);
        }
        return new Column(name, type, values);
    }
}
