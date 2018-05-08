package org.houseflys.jdbc.type.creator;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.column.ByteColumn;

import java.io.IOException;

public class Int8ColumnCreator implements ColumnCreator {

    @Override
    public Column create(int rows, BinaryDeserializer deserializer, String name, String type) throws IOException {
        byte[] data = new byte[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readByte();
        }

        return new ByteColumn(name, type, data);
    }
}
