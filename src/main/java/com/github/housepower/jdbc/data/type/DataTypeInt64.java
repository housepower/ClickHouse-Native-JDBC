package com.github.housepower.jdbc.data.type;

import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.Types;

public class DataTypeInt64 implements IDataType {

    private static final Long DEFAULT_VALUE = 0L;
    private final String name;
    private boolean isUnsigned;

    private static final BigInteger TWO_COMPL_REF = BigInteger.ONE.shiftLeft(64);

    public DataTypeInt64(String name) {
        this.name = name;
        this.isUnsigned = name.startsWith("U");
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
    public Class javaTypeClass() {
        return Long.class;
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        Validate.isTrue(data instanceof Byte || data instanceof Short || data instanceof Integer ||
            data instanceof Long, "Expected Long Parameter, but was " + data.getClass().getSimpleName());

        serializer.writeLong(((Number) data).longValue());
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        long l = deserializer.readLong();
        if (isUnsigned) {
            BigInteger unsigned = new BigInteger(1, longToBytes(l));
            return unsigned;
        }
        return l;
    }

    private static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static BigInteger parseBigIntegerPositive(String num,int bitlen) {
        BigInteger b = new BigInteger(num);
        if (b.compareTo(BigInteger.ZERO) < 0)
            b = b.add(BigInteger.ONE.shiftLeft(bitlen));
        return b;
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        for (Object datum : data) {
            serializeBinary(datum, serializer);
        }
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        Object[] data = new Object[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = this.deserializeBinary(deserializer);
        }
        return data;
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        return lexer.numberLiteral().longValue();
    }
}
