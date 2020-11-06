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

import com.github.housepower.jdbc.data.type.complex.DataTypeNullable;
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ColumnNullable extends Column{
    private List<Byte> nullableSign;
    // data represents netsted column in ColumnArray
    private Column data;

    public ColumnNullable(String name, DataTypeNullable type, Object[] values) {
        super(name, type, values);
        nullableSign = new ArrayList<>(values.length);
        for (int i = 0; i < values.length; i++) {
            nullableSign.set(i, values[i] == null ? (byte) 1 : 0);
        }
        data = ColumnFactory.createColumn(name, type, values);
    }

    @Override
    public void write(Object object) throws IOException, SQLException {
        nullableSign.add(object == null ? (byte) 1 : 0);
        data.write(object);
    }

    @Override
    public void serializeBinaryBulk(BinarySerializer serializer) throws SQLException, IOException {
        serializer.writeStringBinary(name);
        serializer.writeStringBinary(type.name());

        for (byte sign : nullableSign) {
            serializer.writeByte(sign);
        }

        data.serializeBinaryBulk(serializer);
        buffer.writeTo(serializer);
    }

    @Override
    public void clear() {
        super.clear();
        nullableSign.clear();
        data.clear();
    }
}
