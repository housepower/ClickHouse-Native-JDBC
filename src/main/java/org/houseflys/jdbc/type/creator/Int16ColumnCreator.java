package org.houseflys.jdbc.type.creator;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.column.ShortColumn;

import java.io.IOException;

public class Int16ColumnCreator implements ColumnCreator {

    @Override
    public Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer) throws IOException {
        Short[] data = new Short[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readShort();
        }
        return new ShortColumn(name, type, data);
    }
}
