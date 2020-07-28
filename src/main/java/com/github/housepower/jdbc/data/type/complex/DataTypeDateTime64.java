package com.github.housepower.jdbc.data.type.complex;

import com.github.housepower.jdbc.connect.PhysicalInfo.ServerInfo;
import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.StringView;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;
import com.github.housepower.jdbc.settings.SettingKey;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

public class DataTypeDateTime64 implements IDataType {

    public static final Timestamp DEFAULT_VALUE = new Timestamp(0);
    public static final int NANOS_IN_SECOND = 1_000_000_000;
    public static final int MILLIS_IN_SECOND = 1000;
    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS", Locale.ROOT);
    private final String name;
    private final TimeZone timeZone;

    public DataTypeDateTime64(String name, ServerInfo serverInfo) {
        TimeZone timeZone1 = chooseTimeZone(serverInfo);
        this.name = name;
        this.timeZone = timeZone1;
        this.dateTimeFormat.withZone(timeZone1.toZoneId());
    }

    private static TimeZone chooseTimeZone(ServerInfo serverInfo) {
        TimeZone timeZone;
        if (!Boolean.TRUE.equals(serverInfo.getConfigure().settings().get(SettingKey.use_client_time_zone))) {
            timeZone = serverInfo.timeZone();
        } else {
            timeZone = DateTimeZone.getDefault().toTimeZone();
        }
        return timeZone;
    }

    public static DataTypeDateTime64 createDateTime64Type(SQLLexer lexer, ServerInfo serverInfo) throws SQLException {
        if (lexer.isCharacter('(')) {
            Validate.isTrue(lexer.character() == '(');
            int precision = lexer.numberLiteral().intValue();
            if (lexer.isCharacter(',')) {
                Validate.isTrue(lexer.character() == ',');
                Validate.isTrue(lexer.isWhitespace());
                StringView dataTimeZone = lexer.stringLiteral();
                Validate.isTrue(lexer.character() == ')');
                return new DataTypeDateTime64("DateTime64(" + precision + ", " + dataTimeZone + ")", serverInfo);
            }

            Validate.isTrue(lexer.character() == ')');
            return new DataTypeDateTime64("DateTime64(" + precision + ")", serverInfo);
        }
        return new DataTypeDateTime64("DateTime64", serverInfo);
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

        String timeStr = String.format(Locale.ROOT, "%04d-%02d-%02d %02d:%02d:%02d.%09d", year, month, day, hours, minutes, seconds, nanos);
        LocalDateTime dateTime = LocalDateTime.parse(timeStr, dateTimeFormat);
        return Timestamp.valueOf(dateTime);
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
