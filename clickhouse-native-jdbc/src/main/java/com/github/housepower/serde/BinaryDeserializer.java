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

import io.netty.buffer.ByteBuf;

public interface BinaryDeserializer extends SupportCompress, AutoCloseable {

    boolean readBoolean();

    byte readByte();

    long readVarInt();

    short readShortLE();

    int readIntLE();

    long readLongLE();

    float readFloatLE();

    double readDoubleLE();

    ByteBuf readBytes(int size);

    ByteBuf readBytesBinary();

    String readUTF8Binary();

    @Override
    void close();
}
