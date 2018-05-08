package org.houseflys.jdbc.type;

import org.houseflys.jdbc.serializer.BinarySerializer;

import java.io.IOException;

public abstract class Column {

    private final String name;
    private final String type;

    public Column(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return this.name;
    }

    public String typeName() {
        return this.type;
    }

    public abstract Object data(int rows);

    protected abstract void writeImpl(BinarySerializer serializer) throws IOException;

    public void writeTo(BinarySerializer serializer) throws IOException {
        serializer.writeStringBinary(name);
        serializer.writeStringBinary(type);

        writeImpl(serializer);
    }
}
