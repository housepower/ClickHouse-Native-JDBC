package com.github.housepower.jdbc.data.type;

import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

public class DataTypeInt8 implements IDataType {

    private static final Byte DEFAULT_VALUE = 0;
    private final String name;
    private boolean isUnsigned;

    public DataTypeInt8(String name) {
        this.name = name;
        this.isUnsigned = name.startsWith("U");
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int sqlTypeId() {
        return Types.TINYINT;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public Class javaTypeClass() {
        return Byte.class;
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        Validate.isTrue(data instanceof Number, "Expected Byte Parameter, but was " + data.getClass().getSimpleName());
        serializer.writeByte(((Number)data).byteValue());
    }

    @Override
    public Number deserializeBinary(BinaryDeserializer deserializer) throws IOException {
        byte b = deserializer.readByte();
        if (isUnsigned) {
            return (short)(b & 0xff);
        }
        return b;
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        for (Object datum : data) {
            serializeBinary(datum, serializer);
        }
    }

    @Override
    public Number[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws IOException {
        Number[] data = new Number[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializeBinary(deserializer);
        }
        return data;
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        return lexer.numberLiteral().byteValue();
    }
}
