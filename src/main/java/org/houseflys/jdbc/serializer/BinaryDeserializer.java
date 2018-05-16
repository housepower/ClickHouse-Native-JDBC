package org.houseflys.jdbc.serializer;

import java.io.IOException;
import java.net.Socket;

import org.houseflys.jdbc.buffer.BuffedReader;
import org.houseflys.jdbc.buffer.CompressedBuffedReader;
import org.houseflys.jdbc.buffer.SocketBuffedReader;
import org.houseflys.jdbc.misc.Container;

public class BinaryDeserializer {

    private final Container<BuffedReader> container;

    public BinaryDeserializer(Socket socket) throws IOException {
        SocketBuffedReader socketReader = new SocketBuffedReader(socket);
        container = new Container<BuffedReader>(socketReader, new CompressedBuffedReader(socketReader));
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
        int ch1 = container.get().readBinary();
        int ch2 = container.get().readBinary();
        return (short) ((ch1 << 8) + (ch2));
    }

    public int readInt() throws IOException {
        int ch1 = container.get().readBinary();
        int ch2 = container.get().readBinary();
        int ch3 = container.get().readBinary();
        int ch4 = container.get().readBinary();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4));
    }

    public long readLong() throws IOException {
        long ch1 = container.get().readBinary();
        long ch2 = container.get().readBinary();
        long ch3 = container.get().readBinary();
        long ch4 = container.get().readBinary();
        long ch5 = container.get().readBinary();
        long ch6 = container.get().readBinary();
        long ch7 = container.get().readBinary();
        long ch8 = container.get().readBinary();
        return ((ch1 << 56) + (ch2 << 48) + (ch3 << 40) + (ch4 << 32) + (ch5 << 24) + (ch6 << 16) + (ch7 << 8) + (ch8));
    }

    public boolean readBoolean() throws IOException {
        int ch = container.get().readBinary();
        return (ch != 0);
    }

    public String readStringBinary() throws IOException {
        byte[] data = new byte[(int) readVarInt()];
        return container.get().readBinary(data) > 0 ? new String(data) : "";
    }

    public byte readByte() throws IOException {
        return (byte) container.get().readBinary();
    }

    public void maybeEnableCompressed() {
        container.select(true);
    }

    public void maybeDisenableCompressed() {
        container.select(false);
    }
}
