package org.houseflys.jdbc.type;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.column.DoubleColumn;

import java.io.IOException;
import java.sql.SQLException;

public interface ColumnCreator {

    Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer)
        throws IOException, SQLException;
}
