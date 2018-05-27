package org.houseflys.jdbc.type;

import org.houseflys.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;

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

    public String type() {
        return this.type;
    }

    public abstract Object[] data();

    public abstract Object data(int rows);

    public abstract int typeWithSQL() throws SQLException;

    public abstract void writeImpl(BinarySerializer serializer) throws IOException;

    public void writeTo(BinarySerializer serializer) throws IOException {
        serializer.writeStringBinary(name);
        serializer.writeStringBinary(type);

        writeImpl(serializer);
    }
}
