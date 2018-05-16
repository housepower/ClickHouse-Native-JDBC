package org.houseflys.jdbc.type.creator;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.column.ShortColumn;

import java.io.IOException;

public class Int16ColumnCreator implements ColumnCreator {

    @Override
    public Column create(int rows, BinaryDeserializer deserializer, String name, String type) throws IOException {
        short[] data = new short[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readShort();
        }

        return new ShortColumn(name, type, data);
    }
}
