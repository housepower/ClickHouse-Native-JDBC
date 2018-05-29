package org.houseflys.jdbc.type.column;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

/**
 */
public class UUIDColumn extends Column {

    private final long[][] data;
    private final String[] dataStr;

    public UUIDColumn(String name, String type, long[][] data, String[] dataStr) {
        super(name, type);
        this.data = data;
        this.dataStr = dataStr;
    }

    @Override
    public Object[] data() {
        return this.dataStr;
    }

    @Override
    public Object data(int rows) {
        return this.dataStr[rows];
    }

    @Override
    public int typeWithSQL() throws SQLException {
        return Types.VARCHAR;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        for (long[] s : this.data) {
            serializer.writeLong(s[0]);
            serializer.writeLong(s[1]);
        }
    }
}
