package com.github.housepower.jdbc.serializer;

import com.github.housepower.jdbc.buffer.BuffedWriter;
import com.github.housepower.jdbc.buffer.CompressedBuffedWriter;
import com.github.housepower.jdbc.buffer.SocketBuffedWriter;
import com.github.housepower.jdbc.misc.Container;
import com.github.housepower.jdbc.misc.StringView;
import com.github.housepower.jdbc.misc.StringViewCoding;
import com.github.housepower.jdbc.settings.ClickHouseDefines;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class BinarySerializer {
    private final Container<BuffedWriter> container;

    public BinarySerializer(Socket socket) throws IOException {
        SocketBuffedWriter socketWriter = new SocketBuffedWriter(socket);
        container = new Container<BuffedWriter>(socketWriter,
            new CompressedBuffedWriter(ClickHouseDefines.DEFAULT_BUFFER_SIZE, socketWriter));
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

    public void writeByte(byte x) throws IOException {
        container.get().writeBinary(x);
    }

    public void writeBoolean(boolean x) throws IOException {
        writeVarInt((byte) (x ? 1 : 0));
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

    public void writeStringBinary(String binary) throws IOException {
        byte []bs = binary.getBytes();
        writeVarInt(bs.length);
        container.get().writeBinary(bs);
    }

    public void flushToTarget(boolean force) throws IOException {
        container.get().flushToTarget(force);
    }

    public void maybeEnableCompressed() {
        container.select(true);
    }

    public void maybeDisableCompressed() throws IOException {
        container.get().flushToTarget(true);
        container.select(false);
    }

    public void writeFloat(float datum) throws IOException {
        int x = Float.floatToIntBits(datum);
        container.get().writeBinary((byte) (x & 0xFF));
        container.get().writeBinary((byte) ((x >>> 8) & 0xFF));
        container.get().writeBinary((byte) ((x >>> 16) & 0xFF));
        container.get().writeBinary((byte) ((x >>> 24) & 0xFF));
    }

    public void writeDouble(double datum) throws IOException {
        long x = Double.doubleToLongBits(datum);
        container.get().writeBinary((byte) (x & 0xFF));
        container.get().writeBinary((byte) ((x >>> 8) & 0xFF));
        container.get().writeBinary((byte) ((x >>> 16) & 0xFF));
        container.get().writeBinary((byte) ((x >>> 24) & 0xFF));
        container.get().writeBinary((byte) ((x >>> 32) & 0xFF));
        container.get().writeBinary((byte) ((x >>> 40) & 0xFF));
        container.get().writeBinary((byte) ((x >>> 48) & 0xFF));
        container.get().writeBinary((byte) ((x >>> 56) & 0xFF));
    }

    public void writeBytes(byte[] bytes) throws IOException {
        container.get().writeBinary(bytes);
    }

    public void writeStringViewBinary(StringView data) throws IOException, SQLException {
        writeVarInt(data.end() - data.start());
        container.get().writeBinary(StringViewCoding.bytes(data));
    }
}
