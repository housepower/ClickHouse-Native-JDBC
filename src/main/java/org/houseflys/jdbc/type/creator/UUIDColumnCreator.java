package org.houseflys.jdbc.type.creator;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.column.UUIDColumn;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

/**
 */
public class UUIDColumnCreator implements ColumnCreator {

    public UUIDColumnCreator() {
    }

    @Override
    public Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer)
        throws IOException, SQLException {
        long[][] data = new long[rows][2];
        String[] dataStr = new String[rows];

        for (int row = 0; row < rows; row++) {
            data[row][0] = deserializer.readLong();
            data[row][1] = deserializer.readLong();

            dataStr[row] = new UUID(data[row][0], data[row][1]).toString();
        }
        return new UUIDColumn(name, type, data, dataStr);
    }
}
