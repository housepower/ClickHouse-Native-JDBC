package org.houseflys.jdbc.type.column.complex;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

/**
 */
public class Enum16Column extends Column {

    private final short[] data;
    private final String[] dataName;

    public Enum16Column(String name, String type, short[] data, String[] dataName) {
        super(name, type);
        this.data = data;
        this.dataName = dataName;
    }

    @Override
    public Object[] data() {
        return this.dataName;
    }

    @Override
    public Object data(int rows) {
        return this.dataName[rows];
    }

    @Override
    public int typeWithSQL() throws SQLException {
        return Types.VARCHAR;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        for (short s : this.data) {
            serializer.writeShort(s);
        }
    }
}
