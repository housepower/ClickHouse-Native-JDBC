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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.housepower.buffer.ByteArrayWriter;
import com.github.housepower.serde.BinarySerializer;

public class ColumnWriterBufferTest {

    private ColumnWriterBuffer buffer;

    private BinarySerializer serializer;
    private ByteArrayWriter writer;

    private Random rand;

    @BeforeEach
    void setup() {
        buffer = new ColumnWriterBuffer();
        writer = new ByteArrayWriter(1024);
        serializer = new BinarySerializer(writer, false);
        rand = new Random();
    }

    @Test
    void GIVEN_byte_sequence_WHEN_write_THEN_reconstruct_same_byte_sequence() throws IOException {
        // GIVEN
        byte[] sequence = new byte[4 * 1024 * 1024];
        rand.nextBytes(sequence);

        // WHEN
        buffer.column.writeBytes(sequence);

        buffer.writeTo(serializer);

        // THEN
        List<ByteBuffer> bufferList = writer.getBufferList();

        int sequenceIndex = 0;
        for (ByteBuffer buffer : bufferList) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                byte output = buffer.get();
                assertEquals(sequence[sequenceIndex++], output);
            }
        }
    }
}
