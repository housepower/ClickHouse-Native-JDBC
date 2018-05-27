package org.houseflys.jdbc.type.column;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

public class LongColumn extends Column {
    private final Long[] data;

    public LongColumn(String name, String typeName, Long[] data) {
        super(name, typeName);
        this.data = data;
    }

    @Override
    public Long[] data() {
        return data;
    }

    @Override
    public Long data(int rows) {
        return data[rows];
    }

    @Override
    public int typeWithSQL() throws SQLException {
        return Types.BIGINT;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        for (Long datum : data) {
            serializer.writeLong(datum);
        }
    }
}
