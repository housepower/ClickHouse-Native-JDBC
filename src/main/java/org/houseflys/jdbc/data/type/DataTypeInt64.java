package org.houseflys.jdbc.data.type;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.data.IDataType;
import org.houseflys.jdbc.stream.QuotedLexer;
import org.houseflys.jdbc.stream.QuotedToken;
import org.houseflys.jdbc.stream.QuotedTokenType;

public class DataTypeInt64 implements IDataType {

    private static final Long DEFAULT_VALUE = 0L;
    private final String name;

    public DataTypeInt64(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int sqlTypeId() {
        return Types.BIGINT;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        Validate.isTrue(data instanceof Byte || data instanceof Short || data instanceof Integer ||
            data instanceof Long, "Expected Long Parameter, but was " + data.getClass().getSimpleName());

        serializer.writeLong(((Number) data).longValue());
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        return deserializer.readLong();
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        for (Object datum : data) {
            serializeBinary(datum, serializer);
        }
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        Long[] data = new Long[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readLong();
        }
        return data;
    }

    @Override
    public Object deserializeTextQuoted(QuotedLexer lexer) throws SQLException {
        QuotedToken token = lexer.next();
        Validate.isTrue(token.type() == QuotedTokenType.Number, "Expected Number Literal.");
        return Long.valueOf(token.data());
    }

}
