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
import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import java.nio.charset.Charset;

public class CompositeSink implements ISink, SupportCompress, ByteBufHelper {

    private final Switcher<ISink> switcher;
    private final boolean enableCompress;

    public CompositeSink(ISink writer, boolean enableCompress, @Nullable Compressor compressor) {
        this.enableCompress = enableCompress;
        ISink compressWriter = null;
        if (enableCompress) {
            compressWriter = new CompressSink(ClickHouseDefines.SOCKET_SEND_BUFFER_BYTES, writer, compressor);
        }
        switcher = new Switcher<>(compressWriter, writer);
    }

    @Override
    public void writeZero(int len) {
        switcher.get().writeZero(len);
    }

    @Override
    public void writeBoolean(boolean x) {
        switcher.get().writeBoolean(x);
    }

    @Override
    public void writeByte(byte x) {
        switcher.get().writeByte(x);
    }

    @Override
    public void writeShortLE(short i) {
        switcher.get().writeShortLE(i);
    }

    @Override
    public void writeIntLE(int i) {
        switcher.get().writeIntLE(i);
    }

    @Override
    public void writeLongLE(long i) {
        switcher.get().writeLongLE(i);
    }

    @Override
    public void writeVarInt(long x) {
        switcher.get().writeVarInt(x);
    }

    @Override
    public void writeDoubleLE(double datum) {
        switcher.get().writeDoubleLE(datum);
    }

    @Override
    public void writeFloatLE(float datum) {
        switcher.get().writeFloatLE(datum);
    }

    @Override
    public void writeBytes(ByteBuf bytes) {
        switcher.get().writeBytes(bytes);
    }

    @Override
    public void writeCharSequence(CharSequence seq, Charset charset) {
        switcher.get().writeCharSequence(seq, charset);
    }

    @Override
    public void writeBinary(ByteBuf bs) {
        switcher.get().writeBinary(bs);
    }

    @Override
    public void writeCharSequenceBinary(CharSequence seq, Charset charset) {
        switcher.get().writeCharSequenceBinary(seq, charset);
    }

    @Override
    public void writeUTF8Binary(CharSequence utf8) {
        switcher.get().writeUTF8Binary(utf8);
    }

    @Override
    public void maybeEnableCompressed() {
        if (enableCompress) {
            switcher.select(false);
        }
    }

    @Override
    public void maybeDisableCompressed() {
        if (enableCompress) {
            switcher.get().flush(true);
            switcher.select(true);
        }
    }

    @Override
    public void flush(boolean force) {
        switcher.get().flush(force);
    }

    @Override
    public void close() {
        if (enableCompress) {
            switcher.select(false);
        }
        switcher.get().close();
    }
}
