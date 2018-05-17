package org.houseflys.jdbc.type.column.complex;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

public class FixedStringColumn extends Column {
    private final String[] data;

    public FixedStringColumn(String name, String type, String[] data) {
        super(name, type);
        this.data = data;
    }

    @Override
    public String[] data() {
        return data;
    }

    @Override
    public String data(int rows) {
        return data[rows];
    }

    @Override
    public int typeWithSQL() throws SQLException {
        return Types.BLOB;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        for (String datum : data) {
            serializer.writeBytes(datum.getBytes());
        }
    }
}
