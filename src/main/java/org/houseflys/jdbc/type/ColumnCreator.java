package org.houseflys.jdbc.type;

import org.houseflys.jdbc.serializer.BinaryDeserializer;

import java.io.IOException;

public interface ColumnCreator {

    Column create(int rows, BinaryDeserializer deserializer, String name, String type) throws IOException;
}
