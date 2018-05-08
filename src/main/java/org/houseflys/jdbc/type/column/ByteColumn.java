package org.houseflys.jdbc.type.column;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

import java.io.IOException;

public class ByteColumn extends Column {
    private final byte[] data;

    public ByteColumn(String name, String type, byte[] data) {
        super(name, type);
        this.data = data;
    }

    @Override
    public Byte data(int rows) {
        return data[rows];
    }

    @Override
    protected void writeImpl(BinarySerializer serializer) throws IOException {
        for (Byte datum : data) {
            serializer.writeByte(datum);
        }
    }
}
