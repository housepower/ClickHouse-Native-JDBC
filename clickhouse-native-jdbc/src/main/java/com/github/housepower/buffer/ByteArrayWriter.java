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

package com.github.housepower.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class ByteArrayWriter implements BuffedWriter {
    private final int blockSize;
    private ByteBuffer buffer;

    // TODO pooling
    private final List<ByteBuffer> byteBufferList = new LinkedList<>();

    public ByteArrayWriter(int blockSize) {
        this.blockSize = blockSize;
        this.buffer = ByteBuffer.allocate(blockSize);
        this.byteBufferList.add(buffer);
    }

    @Override
    public void writeBinary(byte byt) throws IOException {
        buffer.put(byt);
        flushToTarget(false);
    }

    @Override
    public void writeBinary(byte[] bytes) throws IOException {
        writeBinary(bytes, 0, bytes.length);
    }

    @Override
    public void writeBinary(byte[] bytes, int offset, int length) throws IOException {

        while (buffer.remaining() < length) {
            int num = buffer.remaining();
            buffer.put(bytes, offset, num);
            flushToTarget(true);

            offset += num;
            length -= num;
        }

        buffer.put(bytes, offset, length);
        flushToTarget(false);
    }

    @Override
    public void flushToTarget(boolean force) throws IOException {
        if (buffer.hasRemaining() && !force) {
            return;
        }
        buffer = ByteBuffer.allocate(blockSize);
        byteBufferList.add(buffer);
    }

    public List<ByteBuffer> getBufferList() {
        return byteBufferList;
    }
}
