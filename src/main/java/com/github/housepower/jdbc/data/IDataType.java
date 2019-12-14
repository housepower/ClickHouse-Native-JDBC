package com.github.housepower.jdbc.data;

import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

public interface IDataType {
    String name();

    int sqlTypeId();

    Object defaultValue();

    Class javaTypeClass();

    boolean nullable();

    Object deserializeTextQuoted(SQLLexer lexer) throws SQLException;

    void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException;

    Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException;

    default void serializeBinaryBulk(Iterator<Object> data, BinarySerializer serializer) throws SQLException, IOException {
        while(data.hasNext()) {
            serializeBinary(data.next(), serializer);
        }
    }

    default void serializeBinaryBulk(Object []data, BinarySerializer serializer) throws SQLException, IOException {
        for(Object d : data) {
            serializeBinary(d, serializer);
        }
    }

    Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException;
}
