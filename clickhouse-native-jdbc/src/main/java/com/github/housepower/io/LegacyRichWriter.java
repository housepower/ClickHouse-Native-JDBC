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

import com.github.housepower.misc.Switcher;
import com.github.housepower.settings.ClickHouseDefines;
import io.airlift.compress.Compressor;

import javax.annotation.Nullable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Deprecated
public class LegacyRichWriter implements RichWriter {

    private final Switcher<BinaryWriter> switcher;
    private final boolean enableCompress;

    public LegacyRichWriter(BinaryWriter writer, boolean enableCompress, @Nullable Compressor compressor) {
        this.enableCompress = enableCompress;
        BinaryWriter compressWriter = null;
        if (enableCompress) {
            compressWriter = new CompressBinaryWriter(ClickHouseDefines.SOCKET_SEND_BUFFER_BYTES, writer, compressor);
        }
        switcher = new Switcher<>(compressWriter, writer);
    }

    public void writeVarInt(long x) {
        for (int i = 0; i < 9; i++) {
            byte byt = (byte) (x & 0x7F);

            if (x > 0x7F) {
                byt |= 0x80;
            }

            x >>= 7;
            switcher.get().writeByte(byt);

            if (x == 0) {
                return;
            }
        }
    }

    public void writeByte(byte x) {
        switcher.get().writeByte(x);
    }

    public void writeBoolean(boolean x) {
        writeVarInt((byte) (x ? 1 : 0));
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public void writeShort(short i) {
        // @formatter:off
        switcher.get().writeByte((byte) ((i >> 0) & 0xFF));
        switcher.get().writeByte((byte) ((i >> 8) & 0xFF));
        // @formatter:on
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public void writeInt(int i) {
        // @formatter:off
        switcher.get().writeByte((byte) ((i >> 0)  & 0xFF));
        switcher.get().writeByte((byte) ((i >> 8)  & 0xFF));
        switcher.get().writeByte((byte) ((i >> 16) & 0xFF));
        switcher.get().writeByte((byte) ((i >> 24) & 0xFF));
        // @formatter:on
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public void writeLong(long i) {
        // @formatter:off
        switcher.get().writeByte((byte) ((i >> 0)  & 0xFF));
        switcher.get().writeByte((byte) ((i >> 8)  & 0xFF));
        switcher.get().writeByte((byte) ((i >> 16) & 0xFF));
        switcher.get().writeByte((byte) ((i >> 24) & 0xFF));
        switcher.get().writeByte((byte) ((i >> 32) & 0xFF));
        switcher.get().writeByte((byte) ((i >> 40) & 0xFF));
        switcher.get().writeByte((byte) ((i >> 48) & 0xFF));
        switcher.get().writeByte((byte) ((i >> 56) & 0xFF));
        // @formatter:on
    }

    public void writeUTF8StringBinary(String utf8) {
        writeStringBinary(utf8, StandardCharsets.UTF_8);
    }

    public void writeStringBinary(String data, Charset charset) {
        byte[] bs = data.getBytes(charset);
        writeBytesBinary(bs);
    }

    public void writeBytesBinary(byte[] bs) {
        writeVarInt(bs.length);
        switcher.get().writeBytes(bs);
    }

    public void flushToTarget(boolean force) {
        switcher.get().flushToTarget(force);
    }

    public void maybeEnableCompressed() {
        if (enableCompress) {
            switcher.select(false);
        }
    }

    public void maybeDisableCompressed() {
        if (enableCompress) {
            switcher.get().flushToTarget(true);
            switcher.select(true);
        }
    }

    public void writeFloat(float datum) {
        int x = Float.floatToIntBits(datum);
        writeInt(x);
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public void writeDouble(double datum) {
        long x = Double.doubleToLongBits(datum);
        // @formatter:off
        switcher.get().writeByte((byte) ((x >>> 0)  & 0xFF));
        switcher.get().writeByte((byte) ((x >>> 8)  & 0xFF));
        switcher.get().writeByte((byte) ((x >>> 16) & 0xFF));
        switcher.get().writeByte((byte) ((x >>> 24) & 0xFF));
        switcher.get().writeByte((byte) ((x >>> 32) & 0xFF));
        switcher.get().writeByte((byte) ((x >>> 40) & 0xFF));
        switcher.get().writeByte((byte) ((x >>> 48) & 0xFF));
        switcher.get().writeByte((byte) ((x >>> 56) & 0xFF));
        // @formatter:on
    }

    public void writeBytes(byte[] bytes) {
        switcher.get().writeBytes(bytes);
    }
}
