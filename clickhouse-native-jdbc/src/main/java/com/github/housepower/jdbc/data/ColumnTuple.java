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

import com.github.housepower.jdbc.ClickHouseStruct;
import com.github.housepower.jdbc.data.type.complex.DataTypeTuple;
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;

public class ColumnTuple extends AbstractColumn {
    // data represents netsted column in ColumnArray
    private IColumn []datas;

    public ColumnTuple(String name, DataTypeTuple type, Object []values) {
        super(name, type, values);

        IDataType []types = type.getNestedTypes();
        datas = new IColumn[types.length];
        for (int i = 0; i < types.length; i++) {
            datas[i] = ColumnFactory.createColumn(null, types[i], null);
        }
    }

    @Override
    public void write(Object object) throws IOException, SQLException {
        ClickHouseStruct tuple = (ClickHouseStruct) object;
        for (int i = 0; i < datas.length; i ++) {
            datas[i].write(tuple.getAttributes()[i]);
        }
    }

    @Override
    public void flushToSerializer(BinarySerializer serializer, boolean now) throws SQLException, IOException {
        if (isExported()) {
            serializer.writeStringBinary(name);
            serializer.writeStringBinary(type.name());
        }

        // we should to flush all the nested data to serializer
        // because they are using separate buffers.
        for (int i = 0; i < datas.length; i ++) {
            datas[i].flushToSerializer(serializer, true);
        }

        if (now) {
            buffer.writeTo(serializer);
        }
    }

    @Override
    public void setColumnWriterBuffer(ColumnWriterBuffer buffer) {
        super.setColumnWriterBuffer(buffer);

        for (int i = 0; i < datas.length; i ++) {
            datas[i].setColumnWriterBuffer(new ColumnWriterBuffer());
        }
    }

    @Override
    public void clear() {
    }
}
