package com.github.housepower.jdbc.data.type;

import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DataTypeDate implements IDataType {

    private static final Date DEFAULT_VALUE = new Date(0);
    private static final long MILLIS_DIFF = TimeUnit.DAYS.toMillis(1);

    @Override
    public String name() {
        return "Date";
    }

    @Override
    public int sqlTypeId() {
        return Types.DATE;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public Class javaTypeClass() {
        return Date.class;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        Validate.isTrue(data instanceof Date, "Expected Date Parameter, but was " + data.getClass().getSimpleName());

        serializer.writeShort((short) ((((Date) data).getTime()) / MILLIS_DIFF));
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws IOException {
        return new Date(deserializer.readShort() * MILLIS_DIFF);
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        for (Object datum : data) {
            serializeBinary(datum, serializer);
        }
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws IOException {
        Date[] data = new Date[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = new Date(deserializer.readShort() * MILLIS_DIFF);
        }
        return data;
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        Validate.isTrue(lexer.character() == '\'');
        int year = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == '-');
        int month = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == '-');
        int day = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == '\'');

        return new Date(year - 1900, month - 1, day);
    }

    public static IDataType createDateType(SQLLexer lexer, TimeZone timeZone) {
        return new DataTypeDate();
    }
}
