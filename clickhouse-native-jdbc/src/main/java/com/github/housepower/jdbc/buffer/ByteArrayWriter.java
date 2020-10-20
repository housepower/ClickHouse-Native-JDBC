package com.github.housepower.jdbc.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class ByteArrayWriter implements BuffedWriter{
    private int blockSize;
    private ByteBuffer buffer;

    //TODO pooling
    private List<ByteBuffer> byteBufferList = new LinkedList<>();

    public ByteArrayWriter(int blockSize) {
        this.blockSize = blockSize;
        this.buffer = ByteBuffer.allocate(blockSize);
        byteBufferList.add(buffer);
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

        while (buffer.remaining() < length) {
            int num = buffer.remaining();
            buffer.put(bytes, offset, num);
            flushToTarget(true);

            offset += num;
            length -= num;
        }

        buffer.put(bytes, offset, length);
        flushToTarget(false);
    }

    @Override
    public void flushToTarget(boolean force) throws IOException {
        if (buffer.hasRemaining() && !force) {
            return;
        }
        buffer = ByteBuffer.allocate(blockSize);
        byteBufferList.add(buffer);
    }

    @Deprecated
    private void expend() throws IOException {
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
        byteBufferList.set(0, buffer);
    }

    private static final float expandFactor = 1.5f;

    public List<ByteBuffer> getBufferList() {
        return byteBufferList;
    }
}
