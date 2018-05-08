package org.houseflys.jdbc.serializer;

import java.io.IOException;
import java.net.Socket;

import org.houseflys.jdbc.buffer.BuffedWriter;
import org.houseflys.jdbc.buffer.CompressedBuffedWriter;
import org.houseflys.jdbc.buffer.SocketBuffedWriter;
import org.houseflys.jdbc.misc.Container;

public class BinarySerializer {
    private final Container<BuffedWriter> container;

    public BinarySerializer(Socket socket) throws IOException {
        SocketBuffedWriter socketWriter = new SocketBuffedWriter(socket);
        container = new Container<BuffedWriter>(socketWriter, new CompressedBuffedWriter(1024, socketWriter));
    }

    public void writeVarInt(long x) throws IOException {
        for (int i = 0; i < 9; i++) {
            byte byt = (byte) (x & 0x7F);

            if (x > 0x7F) {
                byt |= 0x80;
            }

            x >>= 7;
            container.get().writeBinary(byt);

            if (x == 0) {
                return;
            }
        }
    }

    public void writeShort(short i) throws IOException {
        container.get().writeBinary((byte) (i & 0xFF));
        container.get().writeBinary((byte) ((i >> 8) & 0xFF));
    }

    public void writeInt(int i) throws IOException {
        container.get().writeBinary((byte) (i & 0xFF));
        container.get().writeBinary((byte) ((i >> 8) & 0xFF));
        container.get().writeBinary((byte) ((i >> 16) & 0xFF));
        container.get().writeBinary((byte) ((i >> 24) & 0xFF));
    }

    public void writeLong(long i) throws IOException {
        container.get().writeBinary((byte) (i & 0xFF));
        container.get().writeBinary((byte) ((i >> 8) & 0xFF));
        container.get().writeBinary((byte) ((i >> 16) & 0xFF));
        container.get().writeBinary((byte) ((i >> 24) & 0xFF));
        container.get().writeBinary((byte) ((i >> 32) & 0xFF));
        container.get().writeBinary((byte) ((i >> 40) & 0xFF));
        container.get().writeBinary((byte) ((i >> 48) & 0xFF));
        container.get().writeBinary((byte) ((i >> 56) & 0xFF));
    }

    public void writeByte(byte x) throws IOException {
        container.get().writeBinary(x);
    }

    public void writeBoolean(boolean compression) throws IOException {
        writeVarInt((byte) (compression ? 1 : 0));
    }

    public void writeStringBinary(String binary) throws IOException {
        writeVarInt(binary.length());
        container.get().writeBinary(binary.getBytes());
    }

    public void flushToTarget(boolean force) throws IOException {
        container.get().flushToTarget(force);
    }

    public void maybeEnableCompressed() {
        container.select(true);
    }

    public void maybeDisenableCompressed() throws IOException {
        container.get().flushToTarget(true);
        container.select(false);
    }
}
