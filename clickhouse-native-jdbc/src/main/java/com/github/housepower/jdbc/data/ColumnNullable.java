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
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ColumnNullable extends AbstractColumn {

    private final List<Byte> nullableSign;
    // data represents nested column in ColumnArray
    private final IColumn data;

    public ColumnNullable(String name, DataTypeNullable type, Object[] values) {
        super(name, type, values);
        nullableSign = new ArrayList<>();
        data = ColumnFactory.createColumn(null, type.getNestedDataType(), null);
    }

    @Override
    public void write(Object object) throws IOException, SQLException {
        nullableSign.add(object == null ? (byte) 1 : 0);
        data.write(object == null ? type.defaultValue() : object);
    }

    @Override
    public void flushToSerializer(BinarySerializer serializer, boolean now) throws IOException, SQLException {
        if (isExported()) {
            serializer.writeUTF8StringBinary(name);
            serializer.writeUTF8StringBinary(type.name());
        }

        for (byte sign : nullableSign) {
            serializer.writeByte(sign);
        }

        if (now)
            buffer.writeTo(serializer);
    }

    @Override
    public void setColumnWriterBuffer(ColumnWriterBuffer buffer) {
        super.setColumnWriterBuffer(buffer);
        data.setColumnWriterBuffer(buffer);
    }
}
