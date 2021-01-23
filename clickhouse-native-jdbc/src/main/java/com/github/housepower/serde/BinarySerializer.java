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

package com.github.housepower.serde;

import com.github.housepower.buffer.BuffedWriter;
import com.github.housepower.buffer.CompressedBuffedWriter;
import com.github.housepower.misc.Switcher;
import com.github.housepower.settings.ClickHouseDefines;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class BinarySerializer {

    private final Switcher<BuffedWriter> switcher;
    private final boolean enableCompress;

    public BinarySerializer(BuffedWriter writer, boolean enableCompress) {
        this.enableCompress = enableCompress;
        BuffedWriter compressWriter = null;
        if (enableCompress) {
            compressWriter = new CompressedBuffedWriter(ClickHouseDefines.SOCKET_SEND_BUFFER_BYTES, writer);
        }
        switcher = new Switcher<>(compressWriter, writer);
    }

    public void writeVarInt(long x) throws IOException {
        for (int i = 0; i < 9; i++) {
            byte byt = (byte) (x & 0x7F);

            if (x > 0x7F) {
                byt |= 0x80;
            }

            x >>= 7;
            switcher.get().writeBinary(byt);

            if (x == 0) {
                return;
            }
        }
    }

    public void writeByte(byte x) throws IOException {
        switcher.get().writeBinary(x);
    }

    public void writeBoolean(boolean x) throws IOException {
        writeVarInt((byte) (x ? 1 : 0));
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public void writeShort(short i) throws IOException {
        // @formatter:off
        switcher.get().writeBinary((byte) ((i >> 0) & 0xFF));
        switcher.get().writeBinary((byte) ((i >> 8) & 0xFF));
        // @formatter:on
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public void writeInt(int i) throws IOException {
        // @formatter:off
        switcher.get().writeBinary((byte) ((i >> 0)  & 0xFF));
        switcher.get().writeBinary((byte) ((i >> 8)  & 0xFF));
        switcher.get().writeBinary((byte) ((i >> 16) & 0xFF));
        switcher.get().writeBinary((byte) ((i >> 24) & 0xFF));
        // @formatter:on
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public void writeLong(long i) throws IOException {
        // @formatter:off
        switcher.get().writeBinary((byte) ((i >> 0)  & 0xFF));
        switcher.get().writeBinary((byte) ((i >> 8)  & 0xFF));
        switcher.get().writeBinary((byte) ((i >> 16) & 0xFF));
        switcher.get().writeBinary((byte) ((i >> 24) & 0xFF));
        switcher.get().writeBinary((byte) ((i >> 32) & 0xFF));
        switcher.get().writeBinary((byte) ((i >> 40) & 0xFF));
        switcher.get().writeBinary((byte) ((i >> 48) & 0xFF));
        switcher.get().writeBinary((byte) ((i >> 56) & 0xFF));
        // @formatter:on
    }

    public void writeUTF8StringBinary(String utf8) throws IOException {
        writeStringBinary(utf8, StandardCharsets.UTF_8);
    }

    public void writeStringBinary(String data, Charset charset) throws IOException {
        byte[] bs = data.getBytes(charset);
        writeBytesBinary(bs);
    }

    public void writeBytesBinary(byte[] bs) throws IOException {
        writeVarInt(bs.length);
        switcher.get().writeBinary(bs);
    }

    public void flushToTarget(boolean force) throws IOException {
        switcher.get().flushToTarget(force);
    }

    public void maybeEnableCompressed() {
        if (enableCompress) {
            switcher.select(false);
        }
    }

    public void maybeDisableCompressed() throws IOException {
        if (enableCompress) {
            switcher.get().flushToTarget(true);
            switcher.select(true);
        }
    }

    public void writeFloat(float datum) throws IOException {
        int x = Float.floatToIntBits(datum);
        writeInt(x);
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public void writeDouble(double datum) throws IOException {
        long x = Double.doubleToLongBits(datum);
        // @formatter:off
        switcher.get().writeBinary((byte) ((x >>> 0)  & 0xFF));
        switcher.get().writeBinary((byte) ((x >>> 8)  & 0xFF));
        switcher.get().writeBinary((byte) ((x >>> 16) & 0xFF));
        switcher.get().writeBinary((byte) ((x >>> 24) & 0xFF));
        switcher.get().writeBinary((byte) ((x >>> 32) & 0xFF));
        switcher.get().writeBinary((byte) ((x >>> 40) & 0xFF));
        switcher.get().writeBinary((byte) ((x >>> 48) & 0xFF));
        switcher.get().writeBinary((byte) ((x >>> 56) & 0xFF));
        // @formatter:on
    }

    public void writeBytes(byte[] bytes) throws IOException {
        switcher.get().writeBinary(bytes);
    }
}
