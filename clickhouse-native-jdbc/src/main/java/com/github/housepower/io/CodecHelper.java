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

public interface CodecHelper {

    // @formatter:off
    int NONE = 0x02;
    int LZ4  = 0x82;
    int ZSTD = 0x90;
    // @formatter:on

    default byte[] getBytes(long l) {
        byte[] memory = new byte[Long.BYTES];
        setLong(memory, 0, l);
        return memory;
    }

    default byte[] getBytes(long[] longArray) {
        byte[] memory = new byte[Long.BYTES * longArray.length];
        for (int index = 0; index < longArray.length; index++) {
            long l = longArray[index];
            setLong(memory, index * Long.BYTES, l);
        }
        return memory;
    }

    default void setLong(byte[] memory, int index, long value) {
        // @formatter:off
        memory[index]     = (byte) (value >>> 56);
        memory[index + 1] = (byte) (value >>> 48);
        memory[index + 2] = (byte) (value >>> 40);
        memory[index + 3] = (byte) (value >>> 32);
        memory[index + 4] = (byte) (value >>> 24);
        memory[index + 5] = (byte) (value >>> 16);
        memory[index + 6] = (byte) (value >>> 8);
        memory[index + 7] = (byte) value;
        // @formatter:on
    }
}
