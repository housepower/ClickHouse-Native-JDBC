package org.houseflys.jdbc.data.type.complex;

import org.houseflys.jdbc.ClickHouseArray;
import org.houseflys.jdbc.data.DataTypeFactory;
import org.houseflys.jdbc.data.IDataType;
import org.houseflys.jdbc.data.ParseResult;
import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.stream.QuotedLexer;
import org.houseflys.jdbc.stream.QuotedToken;
import org.houseflys.jdbc.stream.QuotedTokenType;

import java.io.IOException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataTypeArray implements IDataType {
    private final Array defaultValue;
    private final IDataType elemDataType;
    private final IDataType offsetIDataType = DataTypeFactory.get("UInt64");

    public DataTypeArray(IDataType elemDataType) throws SQLException {
        this.elemDataType = elemDataType;
        this.defaultValue = new ClickHouseArray(new Object[] {elemDataType.defaultValue()});
    }

    @Override
    public Object defaultValue() {
        return defaultValue;
    }

    @Override
    public Object deserializeTextQuoted(QuotedLexer lexer) throws SQLException {
        QuotedToken token = lexer.next();
        Validate.isTrue(token.type() == QuotedTokenType.OpeningSquareBracket);

        List<Object> elems = new ArrayList<Object>();
        for (; ; ) {
            elems.add(elemDataType.deserializeTextQuoted(lexer));
            token = lexer.next();
            Validate.isTrue(token.type() == QuotedTokenType.Comma || token.type() == QuotedTokenType.ClosingSquareBracket);

            if (token.type() == QuotedTokenType.ClosingSquareBracket) {
                return new ClickHouseArray(elems.toArray());
            }
        }
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        Validate.isTrue(data instanceof Array,
            "Can't serializer " + data.getClass().getSimpleName() + " With ArrayDataTypeSerializer.");

        offsetIDataType.serializeBinary(((Object[]) ((Array) data).getArray()).length, serializer);
        elemDataType.serializeBinaryBulk(((Object[]) ((Array) data).getArray()), serializer);
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        Long offset = (Long) offsetIDataType.deserializeBinary(deserializer);
        return elemDataType.deserializeBinaryBulk(offset.intValue(), deserializer);
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {

        for (Object datum : data) {
            Validate.isTrue(datum instanceof Array,
                "Can't serializer " + data.getClass().getSimpleName() + " With StringDataTypeSerializer.");

            Object[] arrayData = (Object[]) ((Array) datum).getArray();
            offsetIDataType.serializeBinary(arrayData.length, serializer);
        }

        for (Object datum : data) {
            Validate.isTrue(datum instanceof Array,
                "Can't serializer " + data.getClass().getSimpleName() + " With StringDataTypeSerializer.");

            Object[] arrayData = (Object[]) ((Array) datum).getArray();
            elemDataType.serializeBinaryBulk(arrayData, serializer);
        }
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws IOException, SQLException {
        ClickHouseArray[] data = new ClickHouseArray[rows];

        Object[] offsets = offsetIDataType.deserializeBinaryBulk(rows, deserializer);
        for (int row = 0, lastOffset = 0; row < rows; row++) {
            Long offset = (Long) offsets[row];
            data[row] = new ClickHouseArray(
                elemDataType.deserializeBinaryBulk(offset.intValue() - lastOffset, deserializer));
            lastOffset = offset.intValue();
        }
        return data;
    }

    public static IDataType createArrayType(QuotedLexer lexer) throws SQLException {
        Validate.isTrue(lexer.next().type() == QuotedTokenType.OpeningRoundBracket);
        IDataType arrayNestedType = DataTypeFactory.get(lexer);
        Validate.isTrue(lexer.next().type() == QuotedTokenType.ClosingRoundBracket);
        return new DataTypeArray(arrayNestedType);
    }
}
