package com.github.housepower.jdbc.data;

import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

public class Column {

    private final String name;
    private final IDataType type;

    private Object[] values;
    private ColumnWriterBuffer buffer;

    public Column(String name, IDataType type) {
        this.name = name;
        this.type = type;
    }

    public Column(String name, IDataType type, Object[] values) {
        this.values = values;
        this.name = name;
        this.type = type;
    }

    public void initWriteBuffer() {
        this.buffer = new ColumnWriterBuffer();
    }

    public String name() {
        return this.name;
    }

    public IDataType type() {
        return this.type;
    }

    public Object values(int rowIdx) {
        return this.values[rowIdx];
    }

    public void write(Object object) throws IOException, SQLException {
        if (type().sqlTypeId() == Types.ARRAY) {
            //TODO
        } else {
            type().serializeBinary(object, buffer.column);
        }
    }


    public void serializeBinaryBulk(BinarySerializer serializer) throws SQLException, IOException {
        serializer.writeStringBinary(name);
        serializer.writeStringBinary(type.name());
        buffer.writeTo(serializer);
    }

    public ColumnWriterBuffer getBuffer() {
        return buffer;
    }
}
