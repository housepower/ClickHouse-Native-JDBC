package org.houseflys.jdbc.type.column;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

public class ShortColumn extends Column {

    private final Short[] data;

    public ShortColumn(String name, String typeName, Short[] data) {
        super(name, typeName);
        this.data = data;
    }

    @Override
    public Short[] data() {
        return data;
    }

    @Override
    public Short data(int rows) {
        return data[rows];
    }

    @Override
    public int typeWithSQL() throws SQLException {
        return Types.SMALLINT;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        for (Short datum : data) {
            serializer.writeShort(datum);
        }
    }
}
