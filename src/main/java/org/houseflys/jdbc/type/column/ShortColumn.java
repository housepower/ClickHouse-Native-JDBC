package org.houseflys.jdbc.type.column;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

import java.io.IOException;

public class ShortColumn extends Column {

    private final short[] data;

    public ShortColumn(String name, String typeName, short[] data) {
        super(name, typeName);
        this.data = data;
    }

    @Override
    public Object data(int rows) {
        return data[rows];
    }

    @Override
    protected void writeImpl(BinarySerializer serializer) throws IOException {
        for (short datum : data) {
            serializer.writeShort(datum);
        }
    }
}
