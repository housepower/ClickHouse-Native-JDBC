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

import com.github.housepower.misc.ExceptionUtil;
import com.github.housepower.misc.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCountUtil;
import okio.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface OkioHelper {
    ///////////////////////////////////////
    /////////////// ByteBuf ///////////////
    ///////////////////////////////////////
    @Deprecated
    default long readVarInt(ByteBuf buf) {
        long number = 0;
        for (int i = 0; i < 9; i++) {
            int byt = buf.readByte();
            number |= (long) (byt & 0x7F) << (7 * i);
            if ((byt & 0x80) == 0) break;
        }
        return number;
    }

    @Deprecated
    default void writeVarInt(ByteBuf buf, long value) {
        for (int i = 0; i < 9; i++) {
            byte byt = (byte) (value & 0x7F);
            if (value > 0x7F) byt |= 0x80;
            value >>= 7;
            buf.writeByte(byt);
            if (value == 0) return;
        }
    }

    @Deprecated
    default String readUTF8Binary(ByteBuf buf) {
        int len = (int) readVarInt(buf);
        // the method has optimized for UTF-8, ISO-8859-1 and US-ASCII
        return buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
    }

    @Deprecated
    default void writeUTF8Binary(ByteBuf buf, CharSequence seq) {
        writeVarInt(buf, ByteBufUtil.utf8Bytes(seq));
        // the method has optimized for UTF-8, ISO-8859-1 and US-ASCII
        buf.writeCharSequence(seq, StandardCharsets.UTF_8);
    }

    @Deprecated
    default CharSequence readCharSequenceBinary(ByteBuf buf, Charset charset) {
        int len = (int) readVarInt(buf);
        return buf.readCharSequence(len, charset);
    }

    @Deprecated
    default void writeCharSequenceBinary(ByteBuf buf, CharSequence seq, Charset charset) {
        ByteBuf temp = NettyUtil.alloc().buffer();
        temp.writeCharSequence(seq, charset);
        writeBinary(buf, temp);
    }

    @Deprecated
    default void writeBinary(ByteBuf buf, ByteBuf data) {
        writeVarInt(buf, data.readableBytes());
        buf.writeBytes(data);
        ReferenceCountUtil.safeRelease(data);
    }


}
