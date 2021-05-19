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
import io.netty.buffer.Unpooled;
import okio.Buffer;
import okio.ByteString;

import java.nio.charset.Charset;

public interface ISink extends AutoCloseable {

    void writeZero(int len);

    void writeBoolean(boolean b);

    void writeByte(byte b);

    void writeShortLE(short s);

    void writeIntLE(int i);

    void writeLongLE(long l);

    void writeVarInt(long v);

    void writeFloatLE(float f);

    void writeDoubleLE(double d);

    void writeBytes(byte[] bytes);

    @Deprecated
    void writeBytes(ByteBuf bytes);

    @Deprecated
    void writeCharSequence(CharSequence seq, Charset charset);

    void writeBinary(byte[] bytes);

    @Deprecated
    void writeBinary(ByteBuf bytes);

    @Deprecated
    void writeCharSequenceBinary(CharSequence seq, Charset charset);

    void writeUTF8Binary(CharSequence utf8);

    void flush(boolean force);

    // explicitly overwrite to suppress Exception
    @Override
    void close();
}
