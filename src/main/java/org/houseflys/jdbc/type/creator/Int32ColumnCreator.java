package org.houseflys.jdbc.type.creator;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.column.IntegerColumn;

import java.io.IOException;

public class Int32ColumnCreator implements ColumnCreator {

    @Override
    public Column create(int rows, BinaryDeserializer deserializer, String name, String type) throws IOException {
        int[] data = new int[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readInt();
        }

        return new IntegerColumn(name, type, data);
    }
}
