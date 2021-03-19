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

import com.github.housepower.misc.ByteBufHelper;
import com.github.housepower.misc.CodecHelper;
import com.github.housepower.misc.NettyUtil;
import io.airlift.compress.Decompressor;
import io.airlift.compress.lz4.Lz4Decompressor;
import io.airlift.compress.zstd.ZstdDecompressor;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import javax.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.github.housepower.settings.ClickHouseDefines.CHECKSUM_LENGTH;
import static com.github.housepower.settings.ClickHouseDefines.COMPRESSION_HEADER_LENGTH;

public class DecompressBinaryReader implements BinaryReader, ByteBufHelper, CodecHelper {

    private final BinaryReader compressedReader;
    private final ByteBuf decompressedBuf;

    private final Decompressor lz4Decompressor = new Lz4Decompressor();
    private final Decompressor zstdDecompressor = new ZstdDecompressor();

    public DecompressBinaryReader(BinaryReader reader) {
        this.compressedReader = reader;
        this.decompressedBuf = NettyUtil.alloc().buffer(); // TODO set a init size
    }

    @Override
    public void skipBytes(int len) {
        maybeDecompress(len);
        decompressedBuf.skipBytes(len);
    }

    @Override
    public boolean readBoolean() {
        maybeDecompress(1);
        return decompressedBuf.readBoolean();
    }

    @Override
    public byte readByte() {
        maybeDecompress(1);
        return decompressedBuf.readByte();
    }

    @Override
    public short readShortLE() {
        maybeDecompress(2);
        return decompressedBuf.readShortLE();
    }

    @Override
    public int readIntLE() {
        maybeDecompress(4);
        return decompressedBuf.readIntLE();
    }

    @Override
    public long readLongLE() {
        maybeDecompress(8);
        return decompressedBuf.readLongLE();
    }

    @Override
    public long readVarInt() {
        maybeDecompress(1); // TODO dangerous
        return readVarInt(decompressedBuf);
    }

    @Override
    public float readFloatLE() {
        maybeDecompress(4);
        return decompressedBuf.readFloatLE();
    }

    @Override
    public double readDoubleLE() {
        maybeDecompress(8);
        return decompressedBuf.readDoubleLE();
    }

    @Override
    public ByteBuf readBytes(int size) {
        maybeDecompress(size);
        return decompressedBuf.readRetainedSlice(size);
    }

    @Override
    public CharSequence readCharSequence(int len, Charset charset) {
        maybeDecompress(len);
        return decompressedBuf.readCharSequence(len, charset);
    }

    @Override
    public ByteBuf readBinary() {
        int len = (int) readVarInt();
        return readBytes(len);
    }

    @Override
    public String readUTF8Binary() {
        int len = (int) readVarInt();
        ByteBuf data = readBytes(len);
        String ret = data.readableBytes() > 0 ? data.readCharSequence(len, StandardCharsets.UTF_8).toString() : "";
        ReferenceCountUtil.safeRelease(data);
        return ret;
    }

    @Override
    public void close() {
        compressedReader.close();
        ReferenceCountUtil.safeRelease(decompressedBuf);
    }

    private void maybeDecompress(int atLeastReadableBytes) {
        if (decompressedBuf.isReadable(atLeastReadableBytes))
            return;

        ByteBuf remaining = null;
        if (decompressedBuf.isReadable()) {
            int remainingLen = decompressedBuf.readableBytes();
            remaining = NettyUtil.alloc().buffer(remainingLen, remainingLen);
            decompressedBuf.readBytes(remaining);
        }
        decompressedBuf.clear();
        if (remaining != null) {
            decompressedBuf.writeBytes(remaining);
            ReferenceCountUtil.safeRelease(remaining);
        }

        compressedReader.skipBytes(CHECKSUM_LENGTH); // TODO validate checksum
        int compressMethod = compressedReader.readByte() & 0x0FF;
        int compressedSize = compressedReader.readIntLE();
        int decompressedSize = compressedReader.readIntLE();
        switch (compressMethod) {
            case LZ4:
                readCompressedData(compressedSize - COMPRESSION_HEADER_LENGTH, decompressedSize, lz4Decompressor);
                break;
            case ZSTD:
                readCompressedData(compressedSize - COMPRESSION_HEADER_LENGTH, decompressedSize, zstdDecompressor);
                break;
            case NONE:
                readCompressedData(compressedSize - COMPRESSION_HEADER_LENGTH, decompressedSize, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown compression magic code: " + compressMethod);
        }
    }

    private void readCompressedData(int compressedSize, int decompressedSize, @Nullable Decompressor decompressor) {
        ByteBuf src = compressedReader.readBytes(compressedSize);
        if (decompressor == null) {
            decompressedBuf.writeBytes(src);
        } else {
            ByteBuffer ret = ByteBuffer.allocate(decompressedSize);
            decompressor.decompress(src.nioBuffer(), ret);
            ret.flip();
            decompressedBuf.writeBytes(ret);
        }
        ReferenceCountUtil.safeRelease(src);
    }
}
