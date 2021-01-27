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
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;

public class ByteBufBinaryReader implements BinaryReader, ByteBufHelper {

    private final ByteBuf buf;

    public ByteBufBinaryReader(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public void skipBytes(int len) {
        buf.skipBytes(len);
    }

    @Override
    public boolean readBoolean() {
        return buf.readBoolean();
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @Override
    public short readShortLE() {
        return buf.readShortLE();
    }

    @Override
    public int readIntLE() {
        return buf.readIntLE();
    }

    @Override
    public long readLongLE() {
        return buf.readLongLE();
    }

    @Override
    public long readVarInt() {
        return readVarInt(buf);
    }

    @Override
    public float readFloatLE() {
        return buf.readFloatLE();
    }

    @Override
    public double readDoubleLE() {
        return buf.readDoubleLE();
    }

    @Override
    public ByteBuf readBytes(int maxLen) {
        int len = Math.min(buf.readableBytes(), maxLen);
        return buf.readRetainedSlice(len);
    }

    @Override
    public ByteBuf readBytesBinary() {
        int len = (int) readVarInt();
        return buf.readSlice(len);
    }

    @Override
    public String readUTF8Binary() {
        ByteBuf data = readBytesBinary();
        return data.readableBytes() > 0 ? data.readCharSequence(data.readableBytes(), StandardCharsets.UTF_8).toString() : "";
    }

    @Override
    public void close() {
        ReferenceCountUtil.safeRelease(buf);
    }
}
