package com.github.housepower.jdbc.data.type.complex;

import com.github.housepower.jdbc.connect.PhysicalInfo.ServerInfo;
import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.DateTimeHelper;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.StringView;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DataTypeDateTime64 implements IDataType {

    public static DataTypeDateTime64 createDateTime64Type(SQLLexer lexer, ServerInfo serverInfo) throws SQLException {
        if (lexer.isCharacter('(')) {
            Validate.isTrue(lexer.character() == '(');
            int precision = lexer.numberLiteral().intValue();
            if (lexer.isCharacter(',')) {
                Validate.isTrue(lexer.character() == ',');
                Validate.isTrue(lexer.isWhitespace());
                StringView dataTimeZone = lexer.stringLiteral();
                Validate.isTrue(lexer.character() == ')');
                return new DataTypeDateTime64("DateTime64(" + precision + ", '" + dataTimeZone + "')", serverInfo);
            }

            Validate.isTrue(lexer.character() == ')');
            return new DataTypeDateTime64("DateTime64(" + precision + ")", serverInfo);
        }
        return new DataTypeDateTime64("DateTime64", serverInfo);
    }

    // Since `Timestamp` is mutable, and `defaultValue()` will return ref instead of a copy for performance,
    // we should ensure DON'T modify it anywhere.
    public static final Timestamp DEFAULT_VALUE = new Timestamp(0);
    public static final int NANOS_IN_SECOND = 1_000_000_000;
    public static final int MILLIS_IN_SECOND = 1000;
    private final String name;
    private final ZoneId tz;

    public DataTypeDateTime64(String name, ServerInfo serverInfo) {
        this.name = name;
        this.tz = DateTimeHelper.chooseTimeZone(serverInfo);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int sqlTypeId() {
        return Types.TIMESTAMP;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public Class javaTypeClass() {
        return Timestamp.class;
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public int getPrecision() {
        return 0;
    }

    @Override
    public int getScale() {
        return 19;
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        StringView dataTypeName = lexer.bareWord();
        Validate.isTrue(dataTypeName.equals("toDateTime64"));
        Validate.isTrue(lexer.character() == '(');
        Validate.isTrue(lexer.character() == '\'');
        int year = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == '-');
        int month = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == '-');
        int day = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.isWhitespace());
        int hours = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == ':');
        int minutes = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == ':');
        int seconds = lexer.longLiteral().intValue();
        Validate.isTrue(lexer.character() == '.');
        int nanos = lexer.longLiteral().intValue();
        Validate.isTrue(lexer.character() == '\'');
        Validate.isTrue(lexer.character() == ')');

        ZonedDateTime zdt = ZonedDateTime.of(year, month, day, hours, minutes, seconds, nanos, tz);
        return Timestamp.from(zdt.toInstant());
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws IOException {
        long value = deserializer.readLong();
        long epochSeconds = value / NANOS_IN_SECOND;
        int nanos = (int) (value % NANOS_IN_SECOND);
        Timestamp timestamp = new Timestamp(epochSeconds * 1000);
        if (nanos != 0)
            timestamp.setNanos(nanos);
        return timestamp;
    }

    //946674184456789
    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws IOException {
        Timestamp timestamp = (Timestamp) data;
        long epochSeconds = timestamp.getTime() / MILLIS_IN_SECOND;
        int nanos = timestamp.getNanos();
        long value = epochSeconds * NANOS_IN_SECOND + nanos;
        serializer.writeLong(value);
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws IOException {
        Timestamp[] data = new Timestamp[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = (Timestamp) deserializeBinary(deserializer);
        }
        return data;
    }
}
