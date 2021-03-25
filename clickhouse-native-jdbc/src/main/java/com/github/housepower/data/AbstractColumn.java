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

public abstract class AbstractColumn implements IColumn {

    protected final String name;
    protected final IDataType<?, ?> type;

    protected Object[] sourceValues;
    protected ByteBufSink sinkBuf;

    public AbstractColumn(String name, IDataType<?, ?> type, Object[] sourceValues) {
        this.name = name;
        this.type = type;
        this.sourceValues = sourceValues;
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
    public Object value(int idx) {
        return sourceValues[idx];
    }

    @Override
    public void setColumnWriterBuffer(ByteBufSink buffer) {
        this.sinkBuf = buffer;
    }

    @Override
    public void close() {
        if (sourceValues != null) {
            sourceValues = new Object[0];
        }
        if (sinkBuf != null) {
            sinkBuf.close();
            sinkBuf = null;
        }
    }
}
