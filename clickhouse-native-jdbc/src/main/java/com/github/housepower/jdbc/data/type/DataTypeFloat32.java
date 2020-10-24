package com.github.housepower.jdbc.data.type;

import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

public class DataTypeFloat32 implements IDataType {

    private static final Float DEFAULT_VALUE = 0.0F;

    @Override
    public String name() {
        return "Float32";
    }

    @Override
    public int sqlTypeId() {
        return Types.FLOAT;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public Class javaTypeClass() {
        return Float.class;
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public int getPrecision() {
        return 8;
    }

    @Override
    public int getScale() {
        return 8;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        serializer.writeFloat((Float) data);
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws IOException {
        return deserializer.readFloat();
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws IOException {
        Float[] data = new Float[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readFloat();
        }
        return data;
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        return lexer.numberLiteral().floatValue();
    }
}
