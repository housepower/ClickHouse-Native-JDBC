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

import com.github.housepower.misc.BytesHelper;
import com.github.housepower.misc.ClickHouseCityHash;
import io.airlift.compress.Compressor;
import io.airlift.compress.lz4.Lz4Compressor;
import io.airlift.compress.zstd.ZstdCompressor;

import java.io.IOException;

import static com.github.housepower.settings.ClickHouseDefines.CHECKSUM_LENGTH;
import static com.github.housepower.settings.ClickHouseDefines.COMPRESSION_HEADER_LENGTH;

public class CompressedBuffedWriter implements BuffedWriter, BytesHelper {

    private final int capacity;
    private final byte[] writtenBuf;
    private final BuffedWriter writer;

    private final Compressor lz4Compressor = new Lz4Compressor();
    private final Compressor zstdCompressor = new ZstdCompressor();

    private int position;

    public CompressedBuffedWriter(int capacity, BuffedWriter writer) {
        this.capacity = capacity;
        this.writtenBuf = new byte[capacity];
        this.writer = writer;
    }

    @Override
    public void writeBinary(byte byt) throws IOException {
        writtenBuf[position++] = byt;
        flushToTarget(false);
    }

    @Override
    public void writeBinary(byte[] bytes, int offset, int length) throws IOException {
        while (remaining() < length) {
            int num = remaining();
            System.arraycopy(bytes, offset, writtenBuf, position, remaining());
            position += num;

            flushToTarget(false);
            offset += num;
            length -= num;
        }

        System.arraycopy(bytes, offset, writtenBuf, position, length);
        position += length;
        flushToTarget(false);
    }

    @Override
    public void flushToTarget(boolean force) throws IOException {
        if (position > 0 && (force || !hasRemaining())) {
            int maxLen = lz4Compressor.maxCompressedLength(position);

            byte[] compressedBuffer = new byte[maxLen + COMPRESSION_HEADER_LENGTH + CHECKSUM_LENGTH];
            int res = lz4Compressor.compress(writtenBuf, 0, position, compressedBuffer, COMPRESSION_HEADER_LENGTH + CHECKSUM_LENGTH, compressedBuffer.length);

            compressedBuffer[CHECKSUM_LENGTH] = (byte) (0x82 & 0xFF);
            int compressedSize = res + COMPRESSION_HEADER_LENGTH;
            System.arraycopy(getBytesLE(compressedSize), 0, compressedBuffer, CHECKSUM_LENGTH + 1, Integer.BYTES);
            System.arraycopy(getBytesLE(position), 0, compressedBuffer, CHECKSUM_LENGTH + Integer.BYTES + 1, Integer.BYTES);

            long[] checksum = ClickHouseCityHash.cityHash128(compressedBuffer, CHECKSUM_LENGTH, compressedSize);
            System.arraycopy(getBytesLE(checksum[0]), 0, compressedBuffer, 0, Long.BYTES);
            System.arraycopy(getBytesLE(checksum[1]), 0, compressedBuffer, Long.BYTES, Long.BYTES);

            writer.writeBinary(compressedBuffer, 0, compressedSize + CHECKSUM_LENGTH);
            position = 0;
        }
    }

    private boolean hasRemaining() {
        return position < capacity;
    }

    private int remaining() {
        return capacity - position;
    }
}
