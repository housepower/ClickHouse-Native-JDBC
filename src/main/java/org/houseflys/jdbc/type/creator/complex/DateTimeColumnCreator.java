package org.houseflys.jdbc.type.creator.complex;


import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.ParseResult;
import org.houseflys.jdbc.type.column.complex.DateTimeColumn;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeColumnCreator implements ColumnCreator {

    private final TimeZone timeZone;

    public DateTimeColumnCreator(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer)
        throws IOException, SQLException {

        Timestamp[] data = new Timestamp[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = new Timestamp(deserializer.readInt() * 1000L);
        }
        return new DateTimeColumn(name, type, data);
    }

    private static final Pattern regex = Pattern.compile("DateTime(\\(\\'(.+)\\'\\))?");

    public static ParseResult parseDateTimeTypeName(String type, int pos) throws SQLException {
        Matcher matcher = regex.matcher(type);
        Validate.isTrue(matcher.find() && matcher.start() == pos, "Unknown data type family:" + type);

        // TODO : server timezone
        String timeZone = matcher.groupCount() > 1 ? matcher.group(1) : "";
        return new ParseResult(matcher.end(), type, new DateTimeColumnCreator(TimeZone.getTimeZone(timeZone)));
    }
}
