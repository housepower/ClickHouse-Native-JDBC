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

import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;

public interface IColumn {
    void write(Object object) throws IOException, SQLException;
    void serializeBinaryBulk(BinarySerializer serializer) throws SQLException, IOException;
    void clear();

    void setColumnWriterBuffer(ColumnWriterBuffer buffer);

    String name();
    IDataType type();
    boolean isExported();
    Object values(int idx);
}


abstract class AbstractColumn implements IColumn {
    protected final String name;
    protected final IDataType type;

    protected Object []values;
    protected ColumnWriterBuffer buffer;

    public AbstractColumn(String name, IDataType type, Object []values) {
        this.name = name;
        this.type = type;
        this.values = values;
    }

    @Override
    public boolean isExported() {
        return name != null;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public IDataType type() {
        return type;
    }

    @Override
    public Object values(int idx) {
        return values[idx];
    }

    @Override
    public void clear() {
        values = new Object[0];
    }

    @Override
    public void setColumnWriterBuffer(ColumnWriterBuffer buffer) {
        this.buffer = buffer;
    }
}