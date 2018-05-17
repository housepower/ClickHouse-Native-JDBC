package org.houseflys.jdbc.type.column;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

public class DateColumn extends Column {

    private Date[] data;

    public DateColumn(String name, String type, Date[] data) {
        super(name, type);
        this.data = data;
    }

    @Override
    public Date[] data() {
        return data;
    }

    @Override
    public Date data(int rows) {
        return data[rows];
    }

    @Override
    public int typeWithSQL() throws SQLException {
        return Types.DATE;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        for (Date datum : data) {
            serializer.writeShort((short) (datum.getTime() / 24 / 60 / 60 / 1000));
        }
    }
}
