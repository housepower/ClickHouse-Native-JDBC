package com.github.housepower.jdbc.data.type;

import com.github.housepower.jdbc.connect.PhysicalInfo;
import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;
import com.github.housepower.jdbc.settings.SettingKey;

import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DataTypeDate implements IDataType {

    private static final Date DEFAULT_VALUE = new Date(0);
    private final DateTimeZone dateTimeZone;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);


    public DataTypeDate(PhysicalInfo.ServerInfo serverInfo) {
        if (!java.lang.Boolean.TRUE
                 .equals(serverInfo.getConfigure().settings().get(SettingKey.use_client_time_zone))) {
            this.dateTimeZone = DateTimeZone.forTimeZone(serverInfo.timeZone());
        } else {
            this.dateTimeZone = DateTimeZone.getDefault();
        }
        this.dateFormat.setTimeZone(this.dateTimeZone.toTimeZone());
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
        long mills = ((Date) data).getTime();
        long daysSinceEpoch = mills / 1000 / 3600 / 24;
        serializer.writeShort((short) daysSinceEpoch);
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws IOException {
        short daysSinceEpoch = deserializer.readShort();
        return new Date(3600L * 24 * 1000 * daysSinceEpoch);
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer)
        throws IOException {
        Date[] data = new Date[rows];
        for (int row = 0; row < rows; row++) {
            short daysSinceEpoch = deserializer.readShort();
            data[row] = new Date(3600L * 24 * 1000 * daysSinceEpoch);
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

        String timeStr = String.format(Locale.ROOT, "%04d-%02d-%02d", year, month, day);
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