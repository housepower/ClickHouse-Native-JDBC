package org.houseflys.jdbc.type.column.complex;

import java.io.IOException;
import java.sql.SQLException;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;

public class NullableColumn extends Column {
    private static final Byte IS_NULL = 1;
    private static final Byte NOT_NULL = 0;

    private final Column nullMap;
    private final Column nestedColumn;

    public NullableColumn(String name, String type, Column nullMap, Column nestedColumn) {
        super(name, type);
        this.nullMap = nullMap;
        this.nestedColumn = nestedColumn;
    }

    @Override
    public Object[] data() {
        Object[] data = nestedColumn.data();
        for (int i = 0; i < data.length; i++) {
            if (IS_NULL.equals(nullMap.data(i))) {
                data[i] = null;
            }
        }
        return data;
    }

    @Override
    public Object data(int rows) {
        return IS_NULL.equals(nullMap.data(rows)) ? null : nestedColumn.data(rows);
    }

    @Override
    public int typeWithSQL() throws SQLException {
        return nestedColumn.typeWithSQL();
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        nullMap.writeImpl(serializer);
        nestedColumn.writeImpl(serializer);
    }
}
