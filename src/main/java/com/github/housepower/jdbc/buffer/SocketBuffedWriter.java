package com.github.housepower.jdbc.buffer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketBuffedWriter implements BuffedWriter {

    private final OutputStream out;

    public SocketBuffedWriter(Socket socket) throws IOException {
        this.out = socket.getOutputStream();
    }

    @Override
    public void writeBinary(byte byt) throws IOException {
        out.write(byt);
    }


    @Override
    public void writeBinary(byte[] bytes) throws IOException {
        out.write(bytes);
    }

    @Override
    public void writeBinary(byte[] bytes, int offset, int length) throws IOException {
        out.write(bytes, offset, length);
    }

    @Override
    public void flushToTarget(boolean force) throws IOException {
        out.flush();
    }
}
