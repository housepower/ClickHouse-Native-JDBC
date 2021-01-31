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

import com.github.housepower.misc.CodecHelper;
import com.github.housepower.misc.ClickHouseCityHash;
import com.github.housepower.misc.NettyUtil;
import io.airlift.compress.Compressor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import static com.github.housepower.settings.ClickHouseDefines.CHECKSUM_LENGTH;
import static com.github.housepower.settings.ClickHouseDefines.COMPRESSION_HEADER_LENGTH;

public class CompressedBuffedWriter implements BuffedWriter, CodecHelper {

    private final int capacity;
    private final BuffedWriter writer;
    private final Compressor compressor;
    private final CompositeByteBuf compositeByteBuf = NettyUtil.alloc().compositeBuffer();

    public CompressedBuffedWriter(int capacity, BuffedWriter writer, Compressor compressor) {
        this.capacity = capacity;
        this.writer = writer;
        this.compressor = compressor;
    }

    @Override
    public void writeBinary(byte byt) {
        compositeByteBuf.addComponents(true, NettyUtil.alloc().buffer(1, 1).writeByte(byt));
        flushToTarget(false);
    }

    @Override
    public void writeBinary(ByteBuf bytes) {
        compositeByteBuf.addComponents(true, bytes);
        flushToTarget(false);
    }

    @Override
    public void flushToTarget(boolean force) {
        if (compositeByteBuf.isReadable() && (force || compositeByteBuf.readableBytes() > capacity)) {
            int maxLen = compressor.maxCompressedLength(compositeByteBuf.readableBytes());
            byte[] writtenBuf = new byte[compositeByteBuf.readableBytes()];
            compositeByteBuf.readBytes(writtenBuf);
            byte[] compressedBuffer = new byte[maxLen + COMPRESSION_HEADER_LENGTH + CHECKSUM_LENGTH];
            int compressedDataLen = compressor.compress(writtenBuf, 0, compositeByteBuf.readableBytes(), compressedBuffer, COMPRESSION_HEADER_LENGTH + CHECKSUM_LENGTH, compressedBuffer.length);

            compressedBuffer[CHECKSUM_LENGTH] = (byte) (0x82 & 0xFF); // TODO not sure if it works for zstd
            int compressedSize = compressedDataLen + COMPRESSION_HEADER_LENGTH;
            System.arraycopy(getBytesLE(compressedSize), 0, compressedBuffer, CHECKSUM_LENGTH + 1, Integer.BYTES);
            System.arraycopy(getBytesLE(compositeByteBuf.readableBytes()), 0, compressedBuffer, CHECKSUM_LENGTH + Integer.BYTES + 1, Integer.BYTES);

            long[] checksum = ClickHouseCityHash.cityHash128(compressedBuffer, CHECKSUM_LENGTH, compressedSize);
            System.arraycopy(getBytesLE(checksum[0]), 0, compressedBuffer, 0, Long.BYTES);
            System.arraycopy(getBytesLE(checksum[1]), 0, compressedBuffer, Long.BYTES, Long.BYTES);

            writer.writeBinary(Unpooled.wrappedBuffer(compressedBuffer, 0, compressedSize + CHECKSUM_LENGTH));
            compositeByteBuf.clear();
        }
    }
}
