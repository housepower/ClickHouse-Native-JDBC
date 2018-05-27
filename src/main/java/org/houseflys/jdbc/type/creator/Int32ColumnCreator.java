package org.houseflys.jdbc.type.creator;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.column.IntegerColumn;

import java.io.IOException;

public class Int32ColumnCreator implements ColumnCreator {

    @Override
    public Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer) throws IOException {
        Integer[] data = new Integer[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readInt();
        }
        return new IntegerColumn(name, type, data);
    }
}
