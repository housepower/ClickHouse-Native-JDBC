package com.github.housepower.jdbc.data;

import com.github.housepower.jdbc.stream.QuotedLexer;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;

public interface IDataType {
    String name();

    int sqlTypeId();

    Object defaultValue();

    Object deserializeTextQuoted(QuotedLexer lexer) throws SQLException;

    void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException;

    Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException;

    void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException;

    Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException;
}
