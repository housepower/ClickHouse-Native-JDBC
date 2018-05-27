package org.houseflys.jdbc.type.creator.complex;

import java.io.IOException;
import java.sql.SQLException;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.ParseResult;

public class NestedColumnCreator implements ColumnCreator {
    public NestedColumnCreator(String type) {
    }

    @Override
    public Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer)
        throws IOException, SQLException {
        return null;
    }

    public static ParseResult parseNestedTypeName(String type, int pos) {
        return null;
    }
}
