package org.houseflys.jdbc.type.column;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

import java.io.IOException;

public class IntegerColumn extends Column {
    private final int[] data;

    public IntegerColumn(String name, String typeName, int[] data) {
        super(name, typeName);
        this.data = data;
    }

    @Override
    public Object data(int rows) {
        return data[rows];
    }

    @Override
    protected void writeImpl(BinarySerializer serializer) throws IOException {
        for (int datum : data) {
            serializer.writeInt(datum);
        }
    }
}
