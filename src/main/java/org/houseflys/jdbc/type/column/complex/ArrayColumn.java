package org.houseflys.jdbc.type.column.complex;

import java.io.IOException;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Types;

import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.wrapper.SQLArray;

public class ArrayColumn extends Column {

    private final Column offsets;
    private final ClickHouseArray[] data;

    public ArrayColumn(String name, String type, Column offsets, ClickHouseArray[] data) {
        super(name, type);

        this.data = data;
        this.offsets = offsets;
    }

    @Override
    public Array[] data() {
        return data;
    }

    @Override
    public Array data(int rows) {
        return data[rows];
    }

    @Override
    public int typeWithSQL() throws SQLException {
        return Types.ARRAY;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        offsets.writeImpl(serializer);
        for (ClickHouseArray datum : data) {
            datum.getNestedColumn().writeImpl(serializer);
        }
    }

    public static class ClickHouseArray extends SQLArray {
        private final int size;
        private final Column nestedColumn;

        public ClickHouseArray(int size, Column nestedColumn) {
            this.size = size;
            this.nestedColumn = nestedColumn;
        }

        @Override
        public void free() throws SQLException {
        }

        @Override
        public Object getArray() throws SQLException {
            return nestedColumn.data();
        }

        public Column getNestedColumn() {
            return nestedColumn;
        }
    }
}
