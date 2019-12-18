package com.github.housepower.jdbc.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 */
public class ByteArrayWriter implements BuffedWriter{
    ByteBuffer buffer;

    public ByteArrayWriter(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public ByteArrayWriter(int size) {
        this.buffer = ByteBuffer.allocate(size);
    }

    @Override
    public void writeBinary(byte byt) throws IOException {
        buffer.put(byt);
        flushToTarget(false);
    }

    @Override
    public void writeBinary(byte[] bytes) throws IOException {
        writeBinary(bytes, 0, bytes.length);
    }

    @Override
    public void writeBinary(byte[] bytes, int offset, int length) throws IOException {
        for (int i = offset, max = offset + length; i < max; ) {
            if (buffer.hasRemaining()) {
                buffer.put(bytes[i]);
                i ++;
            }
            flushToTarget(false);
        }
    }

    @Override
    public void flushToTarget(boolean force) throws IOException {
        if (buffer.hasRemaining()) {
            return;
        }
        int newCapacity = (int) (buffer.capacity() * expandFactor);
        while (newCapacity < (buffer.capacity() + 1)) {
            newCapacity *= expandFactor;
        }
        ByteBuffer expanded = ByteBuffer.allocate(newCapacity);
        expanded.order(buffer.order());
        buffer.flip();
        expanded.put(buffer);
        buffer = expanded;
    }

    private static final float expandFactor = 1.5f;

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
