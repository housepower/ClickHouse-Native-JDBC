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

@Deprecated
public interface BinaryDeserializer {

    long readVarInt() throws IOException;

    short readShort() throws IOException;

    int readInt() throws IOException;

    long readLong() throws IOException;

    boolean readBoolean() throws IOException;

    byte[] readBytesBinary() throws IOException;

    String readUTF8StringBinary() throws IOException;

    byte readByte() throws IOException;

    void maybeEnableCompressed();

    void maybeDisableCompressed();

    float readFloat() throws IOException;

    double readDouble() throws IOException;

    byte[] readBytes(int size) throws IOException;
}
