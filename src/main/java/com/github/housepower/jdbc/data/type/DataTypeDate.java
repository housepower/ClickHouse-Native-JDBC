package com.github.housepower.jdbc.data.type;

import com.github.housepower.jdbc.connect.PhysicalInfo;
import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;
import com.github.housepower.jdbc.settings.SettingKey;

import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class DataTypeDate implements IDataType {

    private static final Date DEFAULT_VALUE = new Date(0);
    private static final long MILLIS_DIFF = TimeUnit.DAYS.toMillis(1);

    private final DateTimeZone dateTimeZone;
    private final LocalDate epochDate;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public DataTypeDate(PhysicalInfo.ServerInfo serverInfo) {
        long now = System.currentTimeMillis();
        if (!java.lang.Boolean.TRUE
            .equals(serverInfo.getConfigure().settings().get(SettingKey.use_client_time_zone))) {
            this.dateTimeZone = DateTimeZone.forTimeZone(serverInfo.timeZone());
        } else {
            this.dateTimeZone = DateTimeZone.getDefault();
        }
        this.dateFormat.setTimeZone(this.dateTimeZone.toTimeZone());
        this.epochDate = new LocalDate(0, dateTimeZone);
    }

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
    public boolean nullable() {
        return false;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer)
        throws SQLException, IOException {
        Validate.isTrue(data instanceof Date,
                        "Expected Date Parameter, but was " + data.getClass().getSimpleName());
        LocalDate localDate = new LocalDate(((Date) data).getTime(), dateTimeZone);
        int daysSinceEpoch = Days.daysBetween(epochDate, localDate).getDays();
        serializer.writeShort((short) daysSinceEpoch);
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws IOException {
        short daysSinceEpoch = deserializer.readShort();
        LocalDate localDate = epochDate.plusDays(daysSinceEpoch);
        return new Date(localDate.toDate().getTime());
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer)
        throws SQLException, IOException {
        for (Object datum : data) {
            serializeBinary(datum, serializer);
        }
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer)
        throws IOException {
        Date[] data = new Date[rows];
        for (int row = 0; row < rows; row++) {
            short daysSinceEpoch = deserializer.readShort();
            LocalDate localDate = epochDate.plusDays(daysSinceEpoch);
            data[row] = new Date(localDate.toDate().getTime());
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

        String timeStr = String.format("%04d-%02d-%02d", year, month, day);
        try {
            java.util.Date date = dateFormat.parse(timeStr);
            return new Date(date.getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static IDataType createDateType(SQLLexer lexer, PhysicalInfo.ServerInfo serverInfo) {
        return new DataTypeDate(serverInfo);
    }
}
