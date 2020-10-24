package com.github.housepower.jdbc.data.type.complex;

import com.github.housepower.jdbc.connect.PhysicalInfo;
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

public class DataTypeDateTime implements IDataType {

    public static IDataType createDateTimeType(SQLLexer lexer, PhysicalInfo.ServerInfo serverInfo) throws SQLException {
        if (lexer.isCharacter('(')) {
            Validate.isTrue(lexer.character() == '(');
            StringView dataTimeZone = lexer.stringLiteral();
            Validate.isTrue(lexer.character() == ')');
            return new DataTypeDateTime("DateTime('" + dataTimeZone + "')", serverInfo);
        }
        return new DataTypeDateTime("DateTime", serverInfo);
    }

    // Since `Timestamp` is mutable, and `defaultValue()` will return ref instead of a copy for performance,
    // we should ensure DON'T modify it anywhere.
    private static final Timestamp DEFAULT_VALUE = new Timestamp(0);
    private final ZoneId tz;

    private final String name;

    public DataTypeDateTime(String name, PhysicalInfo.ServerInfo serverInfo) {
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
        return 10;
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
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
        int seconds = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == '\'');

        ZonedDateTime zdt = ZonedDateTime.of(year, month, day, hours, minutes, seconds, 0, tz);
        return Timestamp.from(zdt.toInstant());
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        serializer.writeInt((int) ((((Timestamp) data).getTime()) / 1000));
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        return new Timestamp(deserializer.readInt() * 1000L);
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        Timestamp[] data = new Timestamp[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = new Timestamp(deserializer.readInt() * 1000L);
        }
        return data;
    }
}
