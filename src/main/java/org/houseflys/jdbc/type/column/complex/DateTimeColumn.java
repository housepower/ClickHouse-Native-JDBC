package org.houseflys.jdbc.type.column.complex;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class DateTimeColumn extends Column {

    private final Timestamp[] data;

    public DateTimeColumn(String name, String type, Timestamp[] data) {
        super(name, type);
        this.data = data;
    }

    @Override
    public Object[] data() {
        return data;
    }

    @Override
    public Object data(int rows) {
        return data[rows];
    }

    @Override
    public int typeWithSQL() throws SQLException {
        return Types.TIMESTAMP;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        for (Timestamp datum : data) {
            serializer.writeInt((int) (datum.getTime() / 1000));
        }
    }
}
