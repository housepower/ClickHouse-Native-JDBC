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

package com.github.housepower.io;

import com.github.housepower.misc.ClickHouseCityHash;
import com.github.housepower.misc.NettyUtil;
import io.airlift.compress.Compressor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

import static com.github.housepower.settings.ClickHouseDefines.CHECKSUM_LENGTH;
import static com.github.housepower.settings.ClickHouseDefines.COMPRESSION_HEADER_LENGTH;

public class CompressSink implements ISink, CodecHelper, ByteBufHelper {

    private final int capacity;
    private final ByteBuf buf;
    private final ISink writer;
    private final Compressor compressor;

    public CompressSink(int capacity, ISink writer, Compressor compressor) {
        this.capacity = capacity;
        this.buf = NettyUtil.alloc().buffer();
        this.writer = writer;
        this.compressor = compressor;
    }

    @Override
    public void writeZero(int len) {
        buf.writeZero(len);
        flush(false);
    }

    @Override
    public void writeBoolean(boolean b) {
        buf.writeBoolean(b);
        flush(false);
    }

    @Override
    public void writeByte(byte byt) {
        buf.writeByte(byt);
        flush(false);
    }

    @Override
    public void writeShortLE(short s) {
        buf.writeShortLE(s);
        flush(false);
    }

    @Override
    public void writeIntLE(int i) {
        buf.writeIntLE(i);
        flush(false);
    }

    @Override
    public void writeLongLE(long l) {
        buf.writeLongLE(l);
        flush(false);
    }

    @Override
    public void writeVarInt(long v) {
        writeVarInt(buf, v);
        flush(false);
    }

    @Override
    public void writeFloatLE(float f) {
        buf.writeFloatLE(f);
        flush(false);
    }

    @Override
    public void writeDoubleLE(double d) {
        buf.writeDoubleLE(d);
        flush(false);
    }

    @Override
    public void writeBytes(ByteBuf bytes) {
        buf.writeBytes(bytes);
        ReferenceCountUtil.safeRelease(bytes);
        flush(false);
    }

    @Override
    public void writeCharSequence(CharSequence seq, Charset charset) {
        buf.writeCharSequence(seq, charset);
        flush(false);
    }

    @Override
    public void writeUTF8Binary(CharSequence utf8) {
        writeUTF8Binary(buf, utf8);
        flush(false);
    }

    @Override
    public void writeCharSequenceBinary(CharSequence seq, Charset charset) {
        ByteBuf buf = NettyUtil.alloc().buffer();
        buf.writeCharSequence(seq, charset);
        writeBinary(buf);
        flush(false);
    }

    @Override
    public void writeBinary(ByteBuf bytes) {
        writeBinary(buf, bytes);
        flush(false);
    }

    @Override
    public void flush(boolean force) {
        if (buf.isReadable() && (force || buf.readableBytes() >= capacity)) {
            byte[] writtenBuf = ByteBufUtil.getBytes(buf);
            buf.clear();

            int maxLen = compressor.maxCompressedLength(writtenBuf.length);
            byte[] compressedBytes = new byte[maxLen + COMPRESSION_HEADER_LENGTH + CHECKSUM_LENGTH];
            int compressedDataLen = compressor.compress(writtenBuf, 0, writtenBuf.length, compressedBytes, COMPRESSION_HEADER_LENGTH + CHECKSUM_LENGTH, compressedBytes.length);

            compressedBytes[CHECKSUM_LENGTH] = (byte) 0x82; // TODO not sure if it works for ZStd
            int compressedSize = compressedDataLen + COMPRESSION_HEADER_LENGTH;
            System.arraycopy(getBytesLE(compressedSize), 0, compressedBytes, CHECKSUM_LENGTH + 1, Integer.BYTES);
            System.arraycopy(getBytesLE(writtenBuf.length), 0, compressedBytes, CHECKSUM_LENGTH + Integer.BYTES + 1, Integer.BYTES);

            long[] checksum = ClickHouseCityHash.cityHash128(compressedBytes, CHECKSUM_LENGTH, compressedSize);
            System.arraycopy(getBytesLE(checksum[0]), 0, compressedBytes, 0, Long.BYTES);
            System.arraycopy(getBytesLE(checksum[1]), 0, compressedBytes, Long.BYTES, Long.BYTES);

            writer.writeBytes(Unpooled.wrappedBuffer(compressedBytes, 0, compressedSize + CHECKSUM_LENGTH));
        }
    }

    @Override
    public void close() {
        writer.close();
        ReferenceCountUtil.safeRelease(buf);
    }
}
