package com.github.housepower.jdbc.buffer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.github.housepower.jdbc.settings.ClickHouseDefines;

public class SocketBuffedWriter implements BuffedWriter {

    private int position;
    private int capacity;

    private final byte[] buf;
    private final OutputStream out;

    public SocketBuffedWriter(Socket socket) throws IOException {
        this.position = 0;
        this.capacity = ClickHouseDefines.DEFAULT_BUFFER_SIZE;

        this.buf = new byte[capacity];
        this.out = socket.getOutputStream();
    }

    @Override
    public void writeBinary(byte byt) throws IOException {
        buf[position++] = byt;
        flushToTarget(false);
    }


    @Override
    public void writeBinary(byte[] bytes) throws IOException {
        writeBinary(bytes, 0, bytes.length);
    }

    @Override
    public void writeBinary(byte[] bytes, int offset, int length) throws IOException {
        for (int i = offset, max = offset + length; i < max; ) {
            if (remaining()) {
                int writtenNumber = Math.min(capacity - position, max - i);
                System.arraycopy(bytes, i, buf, position, writtenNumber);
                i += writtenNumber;
                position += writtenNumber;
            }
            flushToTarget(false);
        }
    }

    @Override
    public void flushToTarget(boolean force) throws IOException {
        if (force || !remaining()) {
            out.write(buf, 0, position);
            position = 0;
            out.flush();
        }
    }

    private boolean remaining() {
        return position < capacity;
    }
}
