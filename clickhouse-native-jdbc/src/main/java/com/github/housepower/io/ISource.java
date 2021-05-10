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
import okio.ByteString;

import java.nio.charset.Charset;

public interface ISource extends AutoCloseable {

    void skipBytes(int len);

    boolean readBoolean();

    byte readByte();

    short readShortLE();

    int readIntLE();

    long readLongLE();

    long readVarInt();

    float readFloatLE();

    double readDoubleLE();

    @Deprecated
    ByteBuf readSlice(int len);

    @Deprecated
    ByteBuf readRetainedSlice(int len);

    ByteString readByteString(int len);

    @Deprecated
    CharSequence readCharSequence(int len, Charset charset);

    @Deprecated
    ByteBuf readSliceBinary();

    @Deprecated
    CharSequence readCharSequenceBinary(Charset charset);

    ByteString readByteStringBinary();

    String readUTF8Binary();

    // explicitly overwrite to suppress Exception
    @Override
    void close();
}
