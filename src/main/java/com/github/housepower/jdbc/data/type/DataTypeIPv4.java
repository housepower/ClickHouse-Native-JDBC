package com.github.housepower.jdbc.data.type;

import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

public class DataTypeIPv4 implements IDataType {

    private static final Integer DEFAULT_VALUE = 0;
    private boolean isUnsigned = true;

    @Override
    public String name() {
        return "IPv4";
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
    public Class javaTypeClass() {
        return Integer.class;
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer)
        throws SQLException, IOException {
        serializer.writeInt(((Long) data).intValue());
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer)
        throws SQLException, IOException {
        int res = deserializer.readInt();
        if (isUnsigned) {
            return 0xffffffffL & res;
        }
        return res;
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer)
        throws SQLException, IOException {
        Object[] data = new Object[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = this.deserializeBinary(deserializer);
        }
        return data;
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        return lexer.numberLiteral().longValue() & 0xffffffff;
    }

}
