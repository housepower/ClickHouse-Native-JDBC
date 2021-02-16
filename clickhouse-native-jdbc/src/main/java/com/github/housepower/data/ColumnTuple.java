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

import com.github.housepower.data.type.complex.DataTypeTuple;
import com.github.housepower.jdbc.ClickHouseStruct;
import com.github.housepower.misc.NettyUtil;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.sql.SQLException;

public class ColumnTuple extends AbstractColumn {

    // data represents nested column in ColumnArray
    private final IColumn[] columnDataArray;

    public ColumnTuple(String name, DataTypeTuple type, Object[] values) {
        super(name, type, values);

        IDataType<?, ?>[] types = type.getNestedTypes();
        columnDataArray = new IColumn[types.length];
        for (int i = 0; i < types.length; i++) {
            columnDataArray[i] = ColumnFactory.createColumn(null, types[i], null);
        }
    }

    @Override
    public void write(Object object) throws IOException, SQLException {
        ClickHouseStruct tuple = (ClickHouseStruct) object;
        for (int i = 0; i < columnDataArray.length; i++) {
            columnDataArray[i].write(tuple.getAttributes()[i]);
        }
    }

    @Override
    public void flush(ByteBuf out, boolean flush) {
        if (isExported()) {
            writeUTF8Binary(out, name);
            writeUTF8Binary(out, type.name());
        }

        // we should to flush all the nested data to serializer
        // because they are using separate buffers.
        for (IColumn data : columnDataArray) {
            data.flush(out, true);
        }

        if (flush) {
            out.writeBytes(buf);
        }
    }

    @Override
    public void setBuf(ByteBuf buf) {
        super.setBuf(buf);
        for (IColumn data : columnDataArray) {
            data.setBuf(NettyUtil.alloc().buffer());
        }
    }

    @Override
    public void clear() {
    }
}
