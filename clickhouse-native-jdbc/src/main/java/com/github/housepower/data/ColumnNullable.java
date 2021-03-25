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

package com.github.housepower.data;

import com.github.housepower.io.ByteBufSink;
import com.github.housepower.data.type.complex.DataTypeNullable;
import com.github.housepower.io.CompositeSink;

import javax.annotation.Nullable;
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
    public void write(@Nullable Object object) throws IOException, SQLException {
        if (object == null) {
            nullableSign.add((byte) 1);
            data.write(type.defaultValue()); // write whatever for padding
        } else {
            nullableSign.add((byte) 0);
            data.write(object);
        }
    }

    @Override
    public void flush(CompositeSink sink, boolean now) throws IOException {
        if (isExported()) {
            sink.writeUTF8Binary(name);
            sink.writeUTF8Binary(type.name());
        }

        for (byte sign : nullableSign) {
            sink.writeByte(sign);
        }

        if (now) {
            sink.writeBytes(sinkBuf.retain());
        }
    }

    @Override
    public void setColumnWriterBuffer(ByteBufSink buffer) {
        buffer.retain();
        data.setColumnWriterBuffer(buffer);
        super.setColumnWriterBuffer(buffer);
    }

    @Override
    public void close() {
        nullableSign.clear();
        data.close();
        super.close();
    }
}
