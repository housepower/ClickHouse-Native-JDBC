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

import io.airlift.compress.Compressor;
import io.airlift.compress.lz4.Lz4Compressor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.github.housepower.settings.ClickHouseDefines.CHECKSUM_LENGTH;
import static com.github.housepower.settings.ClickHouseDefines.COMPRESSION_HEADER_LENGTH;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecompressSourceTest {

    @Test
    public void successfullyReadCompressedData() {
        ByteBuf buf = Unpooled.wrappedBuffer(
                compressedData(new byte[]{1, 2, 3}),
                compressedData(new byte[]{4, 5, 6, 7})
        );
        DecompressSource compressedReader = new DecompressSource(new ByteBufSource(buf));

        assertEquals(compressedReader.readByte(), 1);

        ByteBuf ret = compressedReader.readBytes(5);
        assertEquals(ret.readByte(), 2);
        assertEquals(ret.readByte(), 3);
        assertEquals(ret.readByte(), 4);
        assertEquals(ret.readByte(), 5);
        assertEquals(ret.readByte(), 6);

        assertEquals(compressedReader.readByte(), 7);
    }


    private byte[] compressedData(byte[] bytes) {
        Compressor lz4Compressor = new Lz4Compressor();
        final int maxCompressedLength = lz4Compressor.maxCompressedLength(bytes.length);
        final byte[] compressed = new byte[maxCompressedLength];
        final int compressedLength = lz4Compressor.compress(bytes, 0, bytes.length, compressed, 0, maxCompressedLength);
        byte[] compressData = Arrays.copyOf(compressed, compressedLength);
        byte[] data = new byte[compressData.length + COMPRESSION_HEADER_LENGTH + CHECKSUM_LENGTH];

        data[16] = (byte) (0x82 & 0xFF);
        System.arraycopy(compressData, 0, data, COMPRESSION_HEADER_LENGTH + CHECKSUM_LENGTH, compressData.length);
        System.arraycopy(littleEndian(compressData.length + COMPRESSION_HEADER_LENGTH), 0, data, CHECKSUM_LENGTH + 1, 4);
        System.arraycopy(littleEndian(bytes.length), 0, data, CHECKSUM_LENGTH + 4 + 1, 4);

        return data;
    }

    private byte[] littleEndian(int x) {
        byte[] data = new byte[4];

        data[0] = (byte) (x & 0xFF);
        data[1] = (byte) ((byte) (x >> 8) & 0xFF);
        data[2] = (byte) ((byte) (x >> 16) & 0xFF);
        data[3] = (byte) ((byte) (x >> 24) & 0xFF);

        return data;
    }
}
