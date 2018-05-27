package org.houseflys.jdbc.type.column;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

public class ByteColumn extends Column {
    private final Byte[] data;

    public ByteColumn(String name, String type, Byte[] data) {
        super(name, type);
        this.data = data;
    }

    @Override
    public Byte[] data() {
        return data;
    }

    @Override
    public Byte data(int rows) {
        return data[rows];
    }

    @Override
    public int typeWithSQL() throws SQLException {
        return Types.TINYINT;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        for (Byte datum : data) {
            serializer.writeByte(datum);
        }
    }
}
