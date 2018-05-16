package org.houseflys.jdbc.type.creator;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.column.LongColumn;

import java.io.IOException;

public class Int64ColumnCreator implements ColumnCreator {

    @Override
    public Column create(int rows, BinaryDeserializer deserializer, String name, String type) throws IOException {
        long[] data = new long[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readLong();
        }

        return new LongColumn(name, type, data);
    }
}
