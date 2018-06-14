package com.github.housepower.jdbc.buffer;

import java.io.IOException;

import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

public class CompressedBuffedReader implements BuffedReader {

    private int position;
    private int capacity;
    private byte[] decompressed;

    private final BuffedReader buf;
    private final LZ4FastDecompressor lz4FastDecompressor = LZ4Factory.safeInstance().fastDecompressor();

    public CompressedBuffedReader(BuffedReader buf) {
        this.buf = buf;
    }

    @Override
    public int readBinary() throws IOException {
        if (position == capacity) {
            decompressed = readCompressedData();
            this.position = 0;
            this.capacity = decompressed.length;
        }

        return decompressed[position++];
    }

    @Override
    public int readBinary(byte[] bytes) throws IOException {
        for (int i = 0; i < bytes.length; ) {
            if (position == capacity) {
                decompressed = readCompressedData();
                this.position = 0;
                this.capacity = decompressed.length;
            }

            int pending = bytes.length - i;
            int fillLength = Math.min(pending, capacity - position);

            if (fillLength > 0) {
                System.arraycopy(decompressed, position, bytes, i, fillLength);

                i += fillLength;
                this.position += fillLength;
            }
        }
        return bytes.length;
    }


    private static final int LZ4 = 0x82;
    private static final int NONE = 0x02;
    private static final int ZSTD = 0x90;

    private byte[] readCompressedData() throws IOException {
        //TODO: validate checksum
        buf.readBinary(new byte[16]);

        byte[] compressedHeader = new byte[9];

        if (buf.readBinary(compressedHeader) != 9) {
            //TODO:more detail for exception
            throw new IOException("");
        }

        int method = unsignedByte(compressedHeader[0]);
        int compressedSize = readInt(compressedHeader, 1);
        int decompressedSize = readInt(compressedHeader, 5);

        switch (method) {
            case LZ4:
                return readLZ4CompressedData(compressedSize - 9, decompressedSize);
            case NONE:
                return readNoneCompressedData(decompressedSize);
            default:
                throw new UnsupportedOperationException("Unknown compression method: " + method);
        }
    }

    private byte[] readNoneCompressedData(int size) throws IOException {
        byte[] decompressed = new byte[size];

        if (buf.readBinary(decompressed) != size) {
            throw new IOException("Cannot decompress use None method.");
        }

        return decompressed;
    }

    private byte[] readLZ4CompressedData(int compressedSize, int decompressedSize) throws IOException {
        byte[] compressed = new byte[compressedSize];
        if (buf.readBinary(compressed) == compressedSize) {
            byte[] decompressed = new byte[decompressedSize];

            if (lz4FastDecompressor.decompress(compressed, decompressed) == compressedSize) {
                return decompressed;
            }
        }

        throw new IOException("Cannot decompress use LZ4 method.");
    }

    private int unsignedByte(byte byt) {
        return 0x0FF & byt;
    }


    private int readInt(byte[] bytes, int begin) {
        return (bytes[begin] & 0xFF) | (bytes[begin + 1] & 0XFF) << 8 |
            (bytes[begin + 2] & 0xFF) << 16 | (0xFF & bytes[begin + 3]) << 24;
    }
}
