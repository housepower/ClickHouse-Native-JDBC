package org.houseflys.jdbc.type.column;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

public class FloatColumn extends Column {

    private final Float[] data;

    public FloatColumn(String name, String type, Float[] data) {
        super(name, type);
        this.data = data;
    }

    @Override
    public Float[] data() {
        return data;
    }

    @Override
    public Float data(int rows) {
        return data[rows];
    }

    @Override
    public int typeWithSQL() throws SQLException {
        return Types.FLOAT;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        for (Float datum : data) {
            serializer.writeFloat(datum);
        }
    }
}
