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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataTypeFixedString implements IDataType {

    private final int n;
    private final String defaultValue;

    public DataTypeFixedString(int n) {
        this.n = n;
        char[] data = new char[n];
        for (int i = 0; i < n; i++) {
            data[i] = '\u0000';
        }
        this.defaultValue = new String(data);
    }

    @Override
    public Object defaultValue() {
        return defaultValue;
    }

    @Override
    public Object deserializeTextQuoted(QuotedLexer lexer) throws SQLException {
        QuotedToken token = lexer.next();
        Validate.isTrue(token.type() == QuotedTokenType.StringLiteral, "");
        return token.data();
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        Validate.isTrue(data instanceof String,
            "Can't serializer " + data.getClass().getSimpleName() + " With DateTimeDataTypeSerializer.");
        serializer.writeBytes(((String) data).getBytes());
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        return new String(deserializer.readBytes(n));
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        for (Object datum : data) {
            serializeBinary(datum, serializer);
        }
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        String[] data = new String[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = new String(deserializer.readBytes(n));
        }
        return data;
    }

    public static IDataType createFixedStringType(QuotedLexer lexer) throws SQLException {
        Validate.isTrue(lexer.next().type() == QuotedTokenType.OpeningRoundBracket);
        QuotedToken fixedStringN = lexer.next();
        Validate.isTrue(fixedStringN.type() == QuotedTokenType.Number);
        Validate.isTrue(lexer.next().type() == QuotedTokenType.ClosingRoundBracket);
        return new DataTypeFixedString(Integer.valueOf(fixedStringN.data()));
    }
}
