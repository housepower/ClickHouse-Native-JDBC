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

import com.github.housepower.jdbc.tool.FragmentBuffedReader;
import io.airlift.compress.Compressor;
import io.airlift.compress.lz4.Lz4Compressor;
import org.junit.jupiter.api.Test;

import static com.github.housepower.settings.ClickHouseDefines.CHECKSUM_LENGTH;
import static com.github.housepower.settings.ClickHouseDefines.COMPRESSION_HEADER_LENGTH;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class CompressedBuffedReaderTest {

    @Test
    public void successfullyReadCompressedData() throws Exception {

        CompressedBuffedReader compressedBuffed = new CompressedBuffedReader(
            new FragmentBuffedReader(compressedData(new byte[] {1, 2, 3}), compressedData(new byte[] {4, 5, 6, 7}))
        );

        assertEquals(compressedBuffed.readBinary(), 1);

        byte[] bytes = new byte[5];
        compressedBuffed.readBinary(bytes);

        assertEquals(bytes[0], 2);
        assertEquals(bytes[1], 3);
        assertEquals(bytes[2], 4);
        assertEquals(bytes[3], 5);
        assertEquals(bytes[4], 6);

        assertEquals(compressedBuffed.readBinary(), 7);
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
