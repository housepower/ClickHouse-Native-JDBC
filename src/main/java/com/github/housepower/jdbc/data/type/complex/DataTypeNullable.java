package com.github.housepower.jdbc.data.type.complex;

import java.io.IOException;
import java.sql.SQLException;

import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;
import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.data.DataTypeFactory;
import com.github.housepower.jdbc.stream.QuotedLexer;
import com.github.housepower.jdbc.stream.QuotedToken;
import com.github.housepower.jdbc.stream.QuotedTokenType;

public class DataTypeNullable implements IDataType {
    private static final Byte IS_NULL = 1;
    private static final Byte NON_NULL = 0;

    private final String name;
    private final IDataType nestedDataType;
    private final IDataType nullMapDataType = DataTypeFactory.get("UInt8");

    public DataTypeNullable(String name, IDataType nestedDataType) throws SQLException {
        this.name = name;
        this.nestedDataType = nestedDataType;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int sqlTypeId() {
        return nestedDataType.sqlTypeId();
    }

    @Override
    public Object defaultValue() {
        return nestedDataType.defaultValue();
    }

    @Override
    public Object deserializeTextQuoted(QuotedLexer lexer) throws SQLException {
        QuotedToken token = lexer.next();

        if (token.type() == QuotedTokenType.BareWord) {
            if (token.data() != null && token.data().equalsIgnoreCase("null")) {
                return null;
            }
        }
        lexer.prev();
        return nestedDataType.deserializeTextQuoted(lexer);
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        nullMapDataType.serializeBinary(data == null, serializer);
        this.nestedDataType.serializeBinary(data == null ? nestedDataType.defaultValue() : data, serializer);
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        Object isNull = nullMapDataType.deserializeBinary(deserializer);
        Object dataValues = nestedDataType.deserializeBinary(deserializer);
        return IS_NULL.equals(isNull) ? null : dataValues;
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        Byte[] isNull = new Byte[data.length];
        for (int i = 0; i < data.length; i++) {
            isNull[i] = (data[i] == null ? IS_NULL : NON_NULL);
            data[i] = data[i] == null ? nestedDataType.defaultValue() : data[i];
        }
        nullMapDataType.serializeBinaryBulk(isNull, serializer);
        nestedDataType.serializeBinaryBulk(data, serializer);
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        Object[] nullMap = nullMapDataType.deserializeBinaryBulk(rows, deserializer);

        Object[] data = nestedDataType.deserializeBinaryBulk(rows, deserializer);
        for (int i = 0; i < nullMap.length; i++) {
            if (IS_NULL.equals(nullMap[i])) {
                data[i] = null;
            }
        }
        return data;
    }

    public static IDataType createNullableType(QuotedLexer lexer) throws SQLException {
        Validate.isTrue(lexer.next().type() == QuotedTokenType.OpeningRoundBracket);
        IDataType nullableNestedType = DataTypeFactory.get(lexer);
        Validate.isTrue(lexer.next().type() == QuotedTokenType.ClosingRoundBracket);
        return new DataTypeNullable("Nullable(" + nullableNestedType.name() + ")", nullableNestedType);
    }
}
