package org.houseflys.jdbc.type.creator;

import java.io.IOException;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.column.ByteColumn;

public class Int8ColumnCreator implements ColumnCreator {

    @Override
    public Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer) throws IOException {
        Byte[] data = new Byte[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readByte();
        }
        return new ByteColumn(name, type, data);
    }
}
