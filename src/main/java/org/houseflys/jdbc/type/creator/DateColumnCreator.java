package org.houseflys.jdbc.type.creator;

import java.io.IOException;
import java.sql.Date;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.column.DateColumn;
import org.houseflys.jdbc.type.column.DoubleColumn;

public class DateColumnCreator implements ColumnCreator {
    @Override
    public Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer) throws IOException {
        Date[] data = new Date[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = new Date(deserializer.readShort() * 24L * 60 * 60 * 1000);
        }
        return new DateColumn(name, type, data);
    }
}
