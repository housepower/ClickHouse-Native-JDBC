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

import java.nio.charset.Charset;

public interface BinaryWriter extends AutoCloseable {

    void writeZero(int len);

    void writeBoolean(boolean b);

    void writeByte(byte b);

    void writeShortLE(short s);

    void writeIntLE(int i);

    void writeLongLE(long l);

    void writeVarInt(long v);

    void writeFloatLE(float f);

    void writeDoubleLE(double d);

    void writeBytes(ByteBuf bytes);

    void writeCharSequence(CharSequence seq, Charset charset);

    void writeCharSequenceBinary(CharSequence seq, Charset charset);

    void writeUTF8Binary(CharSequence utf8);

    void writeBinary(ByteBuf bytes);

    void flush(boolean force);

    @Override
    void close();
}
