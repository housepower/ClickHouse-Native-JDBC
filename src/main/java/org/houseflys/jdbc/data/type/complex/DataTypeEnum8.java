package org.houseflys.jdbc.data.type.complex;

import org.houseflys.jdbc.data.IDataType;
import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.stream.QuotedLexer;
import org.houseflys.jdbc.stream.QuotedToken;
import org.houseflys.jdbc.stream.QuotedTokenType;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DataTypeEnum8 implements IDataType {
    private final Map.Entry<Byte, String>[] enumerations;

    public DataTypeEnum8(Map.Entry<Byte, String>[] enumerations) {
        this.enumerations = enumerations;
    }

    @Override
    public Object defaultValue() {
        return enumerations[0].getKey();
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
            "Can't serializer " + data.getClass().getSimpleName() + " With Enum8DataTypeSerializer.");
        for (Map.Entry<Byte, String> enumeration : enumerations) {
            if (enumeration.getValue().equals(data)) {
                serializer.writeByte(enumeration.getKey());
                return;
            }
        }
        throw new SQLException("");
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        byte value = deserializer.readByte();
        for (Map.Entry<Byte, String> enumeration : enumerations) {
            if (enumeration.getKey().equals(value)) {
                return enumeration.getValue();
            }
        }
        throw new SQLException("");
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
            data[row] = (String) deserializeBinary(deserializer);
        }
        return data;
    }

    public static IDataType createEnum8Type(QuotedLexer lexer) throws SQLException {
        Validate.isTrue(lexer.next().type() == QuotedTokenType.OpeningRoundBracket);

        Map<Byte, String> nameWithNumber = new HashMap<Byte, String>();
        for (; ; ) {
            QuotedToken name = lexer.next();
            QuotedToken equals = lexer.next();
            QuotedToken number = lexer.next();

            Validate.isTrue(name.type() == QuotedTokenType.StringLiteral);
            Validate.isTrue(equals.type() == QuotedTokenType.Equals);
            Validate.isTrue(number.type() == QuotedTokenType.Number);

            nameWithNumber.put(Byte.valueOf(number.data()), name.data());

            QuotedToken next = lexer.next();
            Validate.isTrue(next.type() == QuotedTokenType.Comma || next.type() == QuotedTokenType.ClosingRoundBracket);

            if (next.type() == QuotedTokenType.ClosingRoundBracket) {
                return new DataTypeEnum8(nameWithNumber.entrySet().toArray(new Map.Entry[nameWithNumber.size()]));
            }
        }
    }
}
