package org.houseflys.jdbc.data;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.stream.QuotedLexer;

import java.io.IOException;
import java.sql.SQLException;

public interface IDataType {
    Object defaultValue();

    Object deserializeTextQuoted(QuotedLexer lexer) throws SQLException;

    void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException;

    Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException;

    void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException;

    Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException;
}
