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
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ColumnArray extends AbstractColumn {
    private List<Long> offsets;
    // data represents netsted column in ColumnArray
    private List<Column> data;

    public ColumnArray(String name, DataTypeArray type, Object[] values) {
        super(name, type);
        offsets = new ArrayList<>(values.length);
        data = new ArrayList<>(values.length);

        long offset = 0;
        for (int i = 0; i < values.length; i++) {
            offsets.set(i, offset);
            Object []nestData = (Object[])(values[i]);
            data.set(i, ColumnFactory.createColumn(name, type, nestData));

            offset += nestData.length;
        }
    }

    @Override
    public void write(Object object) throws IOException, SQLException {
        type().serializeBinary(object, buffer.column);

        Object []arr = (Object[])object;
        offsets.add(offsets.isEmpty() ? arr.length : offsets.get((int) (size() - 1)) + arr.length);
    }

    @Override
    public void serializeBinaryBulk(BinarySerializer serializer) throws SQLException, IOException {
        serializer.writeStringBinary(name);
        serializer.writeStringBinary(type.name());

        for (long offsetList : offsets) {
            serializer.writeLong(offsetList);
        }

        buffer.writeTo(serializer);
    }

    @Override
    public void clear() {
        offsets.clear();
        data.clear();
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public Object values(int idx) {
        return data.get(idx);
    }
}
