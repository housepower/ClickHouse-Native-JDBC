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

import java.nio.charset.Charset;

// TODO refactor to match ByteBuf
public interface RichWriter {

    void writeVarInt(long x);

    void writeByte(byte x);

    void writeBoolean(boolean x);

    void writeShort(short i);

    void writeInt(int i);

    void writeLong(long i);

    void writeUTF8StringBinary(String utf8);

    void writeStringBinary(String data, Charset charset);

    void writeBytesBinary(byte[] bs);

    void flushToTarget(boolean force);

    void maybeEnableCompressed();

    void maybeDisableCompressed();

    void writeFloat(float datum);

    void writeDouble(double datum);

    void writeBytes(byte[] bytes);
}
