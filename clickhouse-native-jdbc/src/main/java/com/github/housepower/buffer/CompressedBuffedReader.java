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
import java.io.UncheckedIOException;

import com.github.housepower.misc.CodecHelper;
import io.airlift.compress.Decompressor;
import io.airlift.compress.lz4.Lz4Decompressor;
import io.airlift.compress.zstd.ZstdDecompressor;

import javax.annotation.Nullable;

import static com.github.housepower.settings.ClickHouseDefines.CHECKSUM_LENGTH;
import static com.github.housepower.settings.ClickHouseDefines.COMPRESSION_HEADER_LENGTH;

public class CompressedBuffedReader implements BuffedReader, CodecHelper {

    private int position;
    private int capacity;
    private byte[] decompressed;

    private final BuffedReader buf;

    private final Decompressor lz4Decompressor = new Lz4Decompressor();
    private final Decompressor zstdDecompressor = new ZstdDecompressor();

    public CompressedBuffedReader(BuffedReader buf) {
        this.buf = buf;
    }

    @Override
    public int readBinary() throws IOException {
        if (position == capacity) {
            decompressed = readCompressedData();
            this.position = 0;
            this.capacity = decompressed.length;
        }

        return decompressed[position++];
    }

    @Override
    public int readBinary(byte[] bytes) throws IOException {
        for (int i = 0; i < bytes.length; ) {
            if (position == capacity) {
                decompressed = readCompressedData();
                this.position = 0;
                this.capacity = decompressed.length;
            }

            int padding = bytes.length - i;
            int fillLength = Math.min(padding, capacity - position);

            if (fillLength > 0) {
                System.arraycopy(decompressed, position, bytes, i, fillLength);

                i += fillLength;
                this.position += fillLength;
            }
        }
        return bytes.length;
    }

    private byte[] readCompressedData() throws IOException {
        //TODO: validate checksum
        buf.readBinary(new byte[CHECKSUM_LENGTH]);

        byte[] compressedHeader = new byte[COMPRESSION_HEADER_LENGTH];

        if (buf.readBinary(compressedHeader) != COMPRESSION_HEADER_LENGTH) {
            throw new IOException("Invalid compression header");
        }

        int method = compressedHeader[0] & 0x0FF;
        int compressedSize = getIntLE(compressedHeader, 1);
        int decompressedSize = getIntLE(compressedHeader, 5);

        switch (method) {
            case LZ4:
                return readCompressedData(compressedSize - COMPRESSION_HEADER_LENGTH, decompressedSize, lz4Decompressor);
            case ZSTD:
                return readCompressedData(compressedSize - COMPRESSION_HEADER_LENGTH, decompressedSize, zstdDecompressor);
            case NONE:
                return readCompressedData(compressedSize - COMPRESSION_HEADER_LENGTH, decompressedSize, null);
            default:
                throw new UnsupportedOperationException("Unknown compression magic code: " + method);
        }
    }

    private byte[] readCompressedData(int compressedSize, int decompressedSize, @Nullable Decompressor decompressor) throws IOException {
        byte[] compressed = new byte[compressedSize];
        if (buf.readBinary(compressed) == compressedSize) {
            if (decompressor == null)
                return compressed;
            byte[] decompressed = new byte[decompressedSize];
            if (decompressor.decompress(compressed, 0, compressedSize, decompressed, 0, decompressedSize) == decompressedSize)
                return decompressed;
        }
        throw new UncheckedIOException(new IOException("Cannot decompress data."));
    }
}
