package org.houseflys.jdbc.type.creator;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.column.StringColumn;

import java.io.IOException;

public class StringColumnCreator implements ColumnCreator {
    @Override
    public Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer) throws IOException {
        String[] data = new String[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readStringBinary();
        }
        return new StringColumn(name, type, data);
    }
}
