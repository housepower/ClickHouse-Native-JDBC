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

public class ByteBufBinaryReader implements BinaryReader {

    private final ByteBuf buf;

    public ByteBufBinaryReader(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public int readByte() {
        return buf.readUnsignedByte();
    }

    @Override
    public int readBytes(byte[] bytes) {
        int len = Math.min(buf.readableBytes(), bytes.length);
        buf.readBytes(bytes, 0, len);
        return len;
    }
}
