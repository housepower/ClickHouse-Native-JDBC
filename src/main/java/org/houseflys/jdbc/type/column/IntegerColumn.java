package org.houseflys.jdbc.type.column;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

public class IntegerColumn extends Column {
    private final Integer[] data;

    public IntegerColumn(String name, String typeName, Integer[] data) {
        super(name, typeName);
        this.data = data;
    }

    @Override
    public Integer[] data() {
        return data;
    }

    @Override
    public Integer data(int rows) {
        return data[rows];
    }

    @Override
    public int typeWithSQL() throws SQLException {
        return Types.INTEGER;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        for (Integer datum : data) {
            serializer.writeInt(datum);
        }
    }
}
