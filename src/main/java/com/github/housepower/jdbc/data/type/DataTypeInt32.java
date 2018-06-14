package com.github.housepower.jdbc.data.type;

import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;
import com.github.housepower.jdbc.stream.QuotedLexer;
import com.github.housepower.jdbc.stream.QuotedToken;
import com.github.housepower.jdbc.stream.QuotedTokenType;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

public class DataTypeInt32 implements IDataType {

    private static final Integer DEFAULT_VALUE = 0;
    private final String name;

    public DataTypeInt32(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int sqlTypeId() {
        return Types.INTEGER;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        Validate.isTrue(data instanceof Byte || data instanceof Short || data instanceof Integer,
            "Expected Integer Parameter, but was " + data.getClass().getSimpleName());
        serializer.writeInt(((Number) data).intValue());
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        return deserializer.readInt();
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        for (Object datum : data) {
            serializeBinary(datum, serializer);
        }
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        Integer[] data = new Integer[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readInt();
        }
        return data;
    }

    @Override
    public Object deserializeTextQuoted(QuotedLexer lexer) throws SQLException {
        QuotedToken token = lexer.next();
        Validate.isTrue(token.type() == QuotedTokenType.Number, "Expected Number Literal.");
        return Integer.valueOf(token.data());
    }

}
