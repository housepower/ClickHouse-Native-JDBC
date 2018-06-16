package com.github.housepower.jdbc.buffer;

import com.github.housepower.jdbc.misc.ClickHouseCityHash;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;

import java.io.IOException;

public class CompressedBuffedWriter implements BuffedWriter {

    private int position;
    private int capacity;

    private final byte[] writtenBuf;
    private final BuffedWriter writer;
    private final LZ4Compressor lz4Compressor = LZ4Factory.safeInstance().fastCompressor();

    public CompressedBuffedWriter(int capacity, BuffedWriter writer) {
        this.capacity = capacity;
        this.writtenBuf = new byte[capacity];
        this.writer = writer;
    }


    @Override
    public void writeBinary(byte byt) throws IOException {
        writtenBuf[position++] = byt;
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
                System.arraycopy(bytes, i, writtenBuf, position, writtenNumber);
                i += writtenNumber;
                position += writtenNumber;
            }
            flushToTarget(false);
        }
    }

    private static final int COMPRESSION_HEADER_LENGTH = 9;

    @Override
    public void flushToTarget(boolean force) throws IOException {
        if (force || !remaining()) {
            ///TODO: None compressed method
            ///TODO: use buf
            int maxLen = lz4Compressor.maxCompressedLength(position);

            byte[] compressedBuffer = new byte[maxLen + 9 + 16];

            int res = lz4Compressor.compress(writtenBuf, 0, position, compressedBuffer, 9 + 16);

            compressedBuffer[16] = (byte) (0x82 & 0xFF);
            int compressedSize = res + COMPRESSION_HEADER_LENGTH;
            System.arraycopy(littleEndian(compressedSize), 0, compressedBuffer, 17, 4);
            System.arraycopy(littleEndian(position), 0, compressedBuffer, 21, 4);

            long[] checksum = ClickHouseCityHash.cityHash128(compressedBuffer, 16, compressedSize);
            System.arraycopy(littleEndian(checksum[0]), 0, compressedBuffer, 0, 8);
            System.arraycopy(littleEndian(checksum[1]), 0, compressedBuffer, 8, 8);

            writer.writeBinary(compressedBuffer, 0, compressedSize + 16);
            position = 0;
            writer.flushToTarget(force);
        }
    }

    private boolean remaining() {
        return position < capacity;
    }


    private byte[] littleEndian(int x) {
        byte[] data = new byte[4];

        data[0] = (byte) (x & 0xFF);
        data[1] = (byte) ((byte) (x >> 8) & 0xFF);
        data[2] = (byte) ((byte) (x >> 16) & 0xFF);
        data[3] = (byte) ((byte) (x >> 24) & 0xFF);

        return data;
    }

    private byte[] littleEndian(long x) {
        byte[] data = new byte[8];

        data[0] = (byte) (x & 0xFF);
        data[1] = (byte) ((byte) (x >> 8) & 0xFF);
        data[2] = (byte) ((byte) (x >> 16) & 0xFF);
        data[3] = (byte) ((byte) (x >> 24) & 0xFF);
        data[4] = (byte) ((byte) (x >> 32) & 0xFF);
        data[5] = (byte) ((byte) (x >> 40) & 0xFF);
        data[6] = (byte) ((byte) (x >> 48) & 0xFF);
        data[7] = (byte) ((byte) (x >> 56) & 0xFF);

        return data;
    }
}
