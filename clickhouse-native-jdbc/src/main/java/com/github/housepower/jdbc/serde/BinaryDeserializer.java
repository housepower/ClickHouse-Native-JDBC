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

package com.github.housepower.jdbc.serde;

import com.github.housepower.jdbc.buffer.BuffedReader;
import com.github.housepower.jdbc.buffer.CompressedBuffedReader;
import com.github.housepower.jdbc.buffer.SocketBuffedReader;
import com.github.housepower.jdbc.misc.Container;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class BinaryDeserializer {

    private final Container<BuffedReader> container;

    public BinaryDeserializer(Socket socket) throws IOException {
        SocketBuffedReader socketReader = new SocketBuffedReader(socket);
        container = new Container<>(socketReader, new CompressedBuffedReader(socketReader));
    }

    public long readVarInt() throws IOException {
        int number = 0;
        for (int i = 0; i < 9; i++) {
            int byt = container.get().readBinary();

            number |= (byt & 0x7F) << (7 * i);

            if ((byt & 0x80) == 0) {
                break;
            }
        }
        return number;
    }

    public short readShort() throws IOException {
        return (short) ((container.get().readBinary() & 0xFF) + ((container.get().readBinary() & 0xFF) << 8));
    }

    public int readInt() throws IOException {
        return (container.get().readBinary() & 0xFF) + ((container.get().readBinary() & 0xFF) << 8)
            + ((container.get().readBinary() & 0xFF) << 16) + ((container.get().readBinary() & 0xFF) << 24);
    }

    public long readLong() throws IOException {
        return (container.get().readBinary() & 0xFFL) + ((container.get().readBinary() & 0xFFL) << 8)
            + ((container.get().readBinary() & 0xFFL) << 16) + ((container.get().readBinary() & 0xFFL) << 24)
            + ((container.get().readBinary() & 0xFFL) << 32) + ((container.get().readBinary() & 0xFFL) << 40)
            + ((container.get().readBinary() & 0xFFL) << 48) + ((container.get().readBinary() & 0xFFL) << 56);
    }

    public boolean readBoolean() throws IOException {
        return (container.get().readBinary() != 0);
    }

    public byte[] readBytesBinary() throws IOException {
        byte[] data = new byte[(int) readVarInt()];
        container.get().readBinary(data);
        return data;
    }

    public String readUTF8StringBinary() throws IOException {
        byte[] data = new byte[(int) readVarInt()];
        return container.get().readBinary(data) > 0 ? new String(data, StandardCharsets.UTF_8) : "";
    }

    public byte readByte() throws IOException {
        return (byte) container.get().readBinary();
    }

    public void maybeEnableCompressed() {
        container.select(true);
    }

    public void maybeDisableCompressed() {
        container.select(false);
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat((container.get().readBinary() & 0xFF) + ((container.get().readBinary() & 0xFF) << 8)
            + ((container.get().readBinary() & 0xFF) << 16) + ((container.get().readBinary()) << 24));
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(
            (container.get().readBinary() & 0xFFL)
                + ((container.get().readBinary() & 0xFFL) << 8)
                + ((container.get().readBinary() & 0xFFL) << 16)
                + ((container.get().readBinary() & 0xFFL) << 24)
                + ((container.get().readBinary() & 0xFFL) << 32)
                + ((container.get().readBinary() & 0xFFL) << 40)
                + ((container.get().readBinary() & 0xFFL) << 48)
                + ((container.get().readBinary() & 0xFFL) << 56)
        );
    }

    public byte[] readBytes(int size) throws IOException {
        byte[] bytes = new byte[size];
        container.get().readBinary(bytes);
        return bytes;
    }
}
