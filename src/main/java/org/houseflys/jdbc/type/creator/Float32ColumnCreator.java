package org.houseflys.jdbc.type.creator;

import java.io.IOException;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.column.DoubleColumn;
import org.houseflys.jdbc.type.column.FloatColumn;

public class Float32ColumnCreator implements ColumnCreator {

    @Override
    public Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer) throws IOException {
        Float[] data = new Float[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readFloat();
        }
        return new FloatColumn(name, type, data);
    }
}
