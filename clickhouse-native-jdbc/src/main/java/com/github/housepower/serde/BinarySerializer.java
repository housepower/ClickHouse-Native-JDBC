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

import java.io.IOException;
import java.nio.charset.Charset;

@Deprecated
public interface BinarySerializer {

    void writeVarInt(long x) throws IOException;

    void writeByte(byte x) throws IOException;

    void writeBoolean(boolean x) throws IOException;

    void writeShort(short i) throws IOException;

    void writeInt(int i) throws IOException;

    void writeLong(long i) throws IOException;

    void writeUTF8StringBinary(String utf8) throws IOException;

    void writeStringBinary(String data, Charset charset) throws IOException;

    void writeBytesBinary(byte[] bs) throws IOException;

    void flushToTarget(boolean force) throws IOException;

    void maybeEnableCompressed();

    void maybeDisableCompressed() throws IOException;

    void writeFloat(float datum) throws IOException;

    void writeDouble(double datum) throws IOException;

    void writeBytes(byte[] bytes) throws IOException;
}
