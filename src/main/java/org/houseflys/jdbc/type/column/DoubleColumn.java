package org.houseflys.jdbc.type.column;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

public class DoubleColumn extends Column {
    private final Double[] data;

    public DoubleColumn(String name, String type, Double[] data) {
        super(name, type);
        this.data = data;
    }

    @Override
    public Double[] data() {
        return data;
    }

    @Override
    public Double data(int rows) {
        return data[rows];
    }

    @Override
    public int typeWithSQL() throws SQLException {
        return Types.DOUBLE;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        for (Double datum : data) {
            serializer.writeDouble(datum);
        }
    }
}
