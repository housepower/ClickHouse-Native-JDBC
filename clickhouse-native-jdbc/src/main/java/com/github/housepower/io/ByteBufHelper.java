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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface ByteBufHelper {

    ByteBufHelper DEFAULT = new ByteBufHelper() {
    };

    default long readVarInt(ByteBuf buf) {
        long number = 0;
        for (int i = 0; i < 9; i++) {
            int byt = buf.readByte();
            number |= (long) (byt & 0x7F) << (7 * i);
            if ((byt & 0x80) == 0) break;
        }
        return number;
    }

    default void writeVarInt(ByteBuf buf, long value) {
        for (int i = 0; i < 9; i++) {
            byte byt = (byte) (value & 0x7F);
            if (value > 0x7F) byt |= 0x80;
            value >>= 7;
            buf.writeByte(byt);
            if (value == 0) return;
        }
    }

    default String readUTF8Binary(ByteBuf buf) {
        int len = (int) readVarInt(buf);
        // the method has optimized for UTF-8, ISO-8859-1 and US-ASCII
        return buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
    }

    default void writeUTF8Binary(ByteBuf buf, CharSequence seq) {
        writeVarInt(buf, ByteBufUtil.utf8Bytes(seq));
        // the method has optimized for UTF-8, ISO-8859-1 and US-ASCII
        buf.writeCharSequence(seq, StandardCharsets.UTF_8);
    }

    default CharSequence readCharSeqBinary(ByteBuf buf, Charset charset) {
        int len = (int) readVarInt(buf);
        return buf.readCharSequence(len, charset).toString();
    }

    default void writeCharSeqBinary(ByteBuf buf, CharSequence seq, Charset charset) {
        writeVarInt(buf, ByteBufUtil.utf8Bytes(seq));
        buf.writeCharSequence(seq, charset);
    }

    default void writeBinary(ByteBuf buf, byte[] data) {
        writeVarInt(buf, data.length);
        buf.writeBytes(data);
    }
}
