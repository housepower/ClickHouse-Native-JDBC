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

import com.github.housepower.misc.ClickHouseCityHash2;
import com.github.housepower.misc.NettyUtil;
import io.airlift.compress.Compressor;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static com.github.housepower.settings.ClickHouseDefines.COMPRESSION_HEADER_LENGTH;

public class CompressSink implements ISink, CodecHelper, ByteBufHelper {

    private final int capacity;
    private final ByteBuf buf;
    private final ISink sink;
    private final Compressor compressor;

    public CompressSink(int capacity, ISink sink, Compressor compressor) {
        this.capacity = capacity;
        this.buf = NettyUtil.alloc().buffer();
        this.sink = sink;
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
        // TODO optimize for ASCII, UTF8
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
            // 16 bits checksum
            //  1 bit  compressed method
            //  4 bits compressed size
            //  4 bits decompressed size
            //         compressed data
            int decompressedLen = buf.readableBytes();
            int maxCompressedLen = compressor.maxCompressedLength(decompressedLen);
            ByteBuffer compressedData = ByteBuffer.allocate(maxCompressedLen);
            compressor.compress(buf.nioBuffer(), compressedData);
            compressedData.flip();
            int compressedDataLen = compressedData.remaining();

            int compressedSize = COMPRESSION_HEADER_LENGTH + compressedDataLen;
            ByteBuf compressed = NettyUtil.alloc().buffer(compressedSize, compressedSize);

            compressed.writeByte(0x82); // TODO not sure if it works for ZStd
            compressed.writeIntLE(compressedSize);
            compressed.writeIntLE(decompressedLen);
            compressed.writeBytes(compressedData);
            ReferenceCountUtil.safeRelease(compressedData);

            long[] checksum = ClickHouseCityHash2.cityHash128(compressed, 0, compressedSize);
            sink.writeLongLE(checksum[0]);
            sink.writeLongLE(checksum[1]);
            sink.writeBytes(compressed);

            buf.clear();
        }
    }

    @Override
    public void close() {
        sink.close();
        ReferenceCountUtil.safeRelease(buf);
    }
}
