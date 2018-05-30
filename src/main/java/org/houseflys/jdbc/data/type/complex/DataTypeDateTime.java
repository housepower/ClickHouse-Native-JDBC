package org.houseflys.jdbc.data.type.complex;


import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.data.IDataType;
import org.houseflys.jdbc.data.ParseResult;
import org.houseflys.jdbc.stream.QuotedLexer;
import org.houseflys.jdbc.stream.QuotedToken;
import org.houseflys.jdbc.stream.QuotedTokenType;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataTypeDateTime implements IDataType {

    private static final Timestamp DEFAULT_VALUE = new Timestamp(0);

    private final TimeZone timeZone;

    public DataTypeDateTime(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public Object deserializeTextQuoted(QuotedLexer lexer) throws SQLException {
        QuotedToken token = lexer.next();
        Validate.isTrue(token.type() == QuotedTokenType.StringLiteral, "");
        String timestampString = token.data();

        String[] dateAndTime = timestampString.split(" ", 2);
        String[] yearMonthDay = dateAndTime[0].split("-", 3);
        String[] houseMinuteSecond = dateAndTime[1].split(":", 3);

        return new Timestamp(
            Integer.valueOf(yearMonthDay[0]) - 1900,
            Integer.valueOf(yearMonthDay[1]) - 1,
            Integer.valueOf(yearMonthDay[2]),
            Integer.valueOf(houseMinuteSecond[0]),
            Integer.valueOf(houseMinuteSecond[1]),
            Integer.valueOf(houseMinuteSecond[2]),
            0);
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        Validate.isTrue(data instanceof Timestamp,
            "Can't serializer " + data.getClass().getSimpleName() + " With DateTimeDataTypeSerializer.");
        serializer.writeInt((int) (((Timestamp) data).getTime() / 1000));
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        return new Timestamp(deserializer.readInt() * 1000L);
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        for (Object datum : data) {
            serializeBinary(datum, serializer);
        }
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        Timestamp[] data = new Timestamp[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = new Timestamp(deserializer.readInt() * 1000L);
        }
        return data;
    }

    public static IDataType createDateTimeType(QuotedLexer lexer) throws SQLException {
        if (lexer.next().type() == QuotedTokenType.OpeningRoundBracket) {
            QuotedToken timeZoneToken = lexer.next();
            Validate.isTrue(timeZoneToken.type() == QuotedTokenType.StringLiteral);
            return new DataTypeDateTime(TimeZone.getTimeZone(timeZoneToken.data()));
        }
        return new DataTypeDateTime(TimeZone.getDefault());
    }
}
