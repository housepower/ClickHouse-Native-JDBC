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

import com.github.housepower.jdbc.buffer.ByteArrayWriter;
import com.github.housepower.jdbc.serde.BinarySerializer;
import com.github.housepower.jdbc.settings.ClickHouseDefines;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 */
public class ColumnWriterBuffer {

    private final ByteArrayWriter columnWriter;

    public BinarySerializer column;

    public ColumnWriterBuffer() {
        this.columnWriter = new ByteArrayWriter(ClickHouseDefines.COLUMN_BUFFER_BYTES);
        this.column = new BinarySerializer(columnWriter, false);
    }

    public void writeTo(BinarySerializer serializer) throws IOException {
        for (ByteBuffer buffer : columnWriter.getBufferList()) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                serializer.writeByte(buffer.get());
            }
        }
    }
}