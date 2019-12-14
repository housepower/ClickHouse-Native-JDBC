package com.github.housepower.jdbc.data;

import com.github.housepower.jdbc.misc.Slice;
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;

public class Column {

    private final String name;
    private final IDataType type;
    private final Slice rowsData;
    private final int rows;

    private boolean isConstant;

    public Column(String name, IDataType type, Slice rowsData) {
        this.name = name;
        this.type = type;
        this.rowsData = rowsData;
        this.rows = rowsData.size();
    }

    // const column
    public Column(String name, IDataType type, Object constData, int rows) {
        this.name = name;
        this.type = type;
        this.isConstant = true;
        this.rows = rows;
        this.rowsData = new Slice(1);
        this.rowsData.add(constData);
    }

    public String name() {
        return this.name;
    }

    public IDataType type() {
        return this.type;
    }

    public Slice data() {
        return rowsData;
    }

    public Object data(int rows) {
        if (isConstant) {
            return rowsData.get(0);
        }
        return rowsData.get(rows);
    }

    public void serializeBinaryBulk(BinarySerializer serializer) throws SQLException, IOException {
        serializer.writeStringBinary(name);
        serializer.writeStringBinary(type.name());

        if (isConstant) {
                type.serializeBinary(rowsData.get(0), serializer);
        } else {
            for (int i = 0; i < rows; i++) {
                type.serializeBinary(rowsData.get(i), serializer);
            }
        }
    }
}
