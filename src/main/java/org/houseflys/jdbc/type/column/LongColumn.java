package org.houseflys.jdbc.type.column;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

import java.io.IOException;

public class LongColumn extends Column {
    private final long[] data;

    public LongColumn(String name, String typeName, long[] data) {
        super(name, typeName);
        this.data = data;
    }

    @Override
    public Object data(int rows) {
        return data[rows];
    }

    @Override
    protected void writeImpl(BinarySerializer serializer) throws IOException {
        for (long datum : data) {
            serializer.writeLong(datum);
        }
    }
}
