package org.houseflys.jdbc.type.column;

import java.io.IOException;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

public class StringColumn extends Column {
    private final String[] data;

    public StringColumn(String name, String type, String[] data) {
        super(name, type);
        this.data = data;
    }

    @Override
    public Object data(int rows) {
        return data[rows];
    }

    @Override
    protected void writeImpl(BinarySerializer serializer) throws IOException {
        for (String datum : data) {
            serializer.writeStringBinary(datum);
        }
    }
}
