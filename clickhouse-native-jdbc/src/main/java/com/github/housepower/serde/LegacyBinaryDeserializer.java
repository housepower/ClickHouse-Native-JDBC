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

import com.github.housepower.io.BinaryReader;
import com.github.housepower.io.DecompressBinaryReader;
import com.github.housepower.misc.Switcher;
import io.netty.buffer.ByteBuf;

public class LegacyBinaryDeserializer implements BinaryDeserializer, SupportCompress {

    private final Switcher<BinaryReader> switcher;
    private final boolean enableCompress;

    public LegacyBinaryDeserializer(BinaryReader buffedReader, boolean enableCompress) {
        this.enableCompress = enableCompress;
        BinaryReader compressedReader = null;
        if (enableCompress) {
            compressedReader = new DecompressBinaryReader(buffedReader);
        }
        switcher = new Switcher<>(compressedReader, buffedReader);
    }

    @Override
    public long readVarInt() {
        return switcher.get().readVarInt();
    }

    @Override
    public short readShortLE() {
        return switcher.get().readShortLE();
    }

    @Override
    public int readIntLE() {
        return switcher.get().readIntLE();
    }

    @Override
    public long readLongLE() {
        return switcher.get().readLongLE();
    }

    @Override
    public boolean readBoolean() {
        return switcher.get().readBoolean();
    }

    @Override
    public ByteBuf readBytesBinary() {
        return switcher.get().readBytesBinary();
    }

    @Override
    public String readUTF8Binary() {
        return switcher.get().readUTF8Binary();
    }

    @Override
    public byte readByte() {
        return switcher.get().readByte();
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
            switcher.select(true);
        }
    }

    @Override
    public float readFloatLE() {
        return switcher.get().readFloatLE();
    }

    @Override
    public double readDoubleLE() {
        return switcher.get().readDoubleLE();
    }

    @Override
    public ByteBuf readBytes(int size) {
        return switcher.get().readBytes(size);
    }

    @Override
    public void close() {
        if (enableCompress) {
            switcher.select(false);
        }
        switcher.get().close();
    }
}
