package org.houseflys.jdbc.data.type.complex;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.data.IDataType;
import org.houseflys.jdbc.data.DataTypeFactory;
import org.houseflys.jdbc.data.ParseResult;
import org.houseflys.jdbc.ClickHouseStruct;
import org.houseflys.jdbc.stream.QuotedLexer;
import org.houseflys.jdbc.stream.QuotedToken;
import org.houseflys.jdbc.stream.QuotedTokenType;

public class DataTypeTuple implements IDataType {

    private final IDataType[] elemsDataType;

    public DataTypeTuple(IDataType[] elemsDataType) {
        this.elemsDataType = elemsDataType;
    }

    @Override
    public Object defaultValue() {
        Object[] attrs = new Object[elemsDataType.length];
        for (int i = 0; i < elemsDataType.length; i++) {
            attrs[i] = elemsDataType[i].defaultValue();
        }
        return new ClickHouseStruct(attrs);
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        Validate.isTrue(data instanceof Struct,
            "Can't serializer " + data.getClass().getSimpleName() + " With TupleDataTypeSerializer.");

        for (int i = 0; i < elemsDataType.length; i++) {
            elemsDataType[i].serializeBinary(((Struct) data).getAttributes()[i], serializer);
        }
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        Object[] attrs = new Object[elemsDataType.length];
        for (int i = 0; i < elemsDataType.length; i++) {
            attrs[i] = elemsDataType[i].deserializeBinary(deserializer);
        }
        return new ClickHouseStruct(attrs);
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        for (int i = 0; i < elemsDataType.length; i++) {
            Object[] elemsData = new Object[data.length];
            for (int row = 0; row < data.length; row++) {
                Validate.isTrue(data[row] instanceof Struct,
                    "Can't serializer " + data.getClass().getSimpleName() + " With TupleDataTypeSerializer.");
                elemsData[row] = ((Struct) data[row]).getAttributes()[i];
            }
            elemsDataType[i].serializeBinaryBulk(elemsData, serializer);
        }
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        Object[][] rowsWithElems = getRowsWithElems(rows, deserializer);

        Struct[] rowsData = new Struct[rows];
        for (int row = 0; row < rows; row++) {
            Object[] elemsData = new Object[elemsDataType.length];

            for (int elemIndex = 0; elemIndex < elemsDataType.length; elemIndex++) {
                elemsData[elemIndex] = rowsWithElems[elemIndex][row];
            }
            rowsData[row] = new ClickHouseStruct(elemsData);
        }
        return rowsData;
    }

    private Object[][] getRowsWithElems(int rows, BinaryDeserializer deserializer) throws IOException, SQLException {
        Object[][] rowsWithElems = new Object[elemsDataType.length][];
        for (int index = 0; index < elemsDataType.length; index++) {
            rowsWithElems[index] = elemsDataType[index].deserializeBinaryBulk(rows, deserializer);
        }
        return rowsWithElems;
    }

    @Override
    public Object deserializeTextQuoted(QuotedLexer lexer) throws SQLException {
        QuotedToken token = lexer.next();
        Validate.isTrue(token.type() == QuotedTokenType.OpeningRoundBracket);

        Object[] elems = new Object[elemsDataType.length];
        for (int i = 0; i < elemsDataType.length; i++) {
            if (i > 0) {
                Validate.isTrue(lexer.next().type() == QuotedTokenType.Comma);
            }
            elems[i] = elemsDataType[i].deserializeTextQuoted(lexer);
        }
        Validate.isTrue(lexer.next().type() == QuotedTokenType.ClosingRoundBracket);
        return new ClickHouseStruct(elems);
    }

    public static DataTypeTuple createTupleType(QuotedLexer lexer) throws SQLException {
        Validate.isTrue(lexer.next().type() == QuotedTokenType.OpeningRoundBracket);
        List<IDataType> tupleNestedDataType = new ArrayList<IDataType>();

        for (; ; ) {
            tupleNestedDataType.add(DataTypeFactory.get(lexer));
            QuotedToken token = lexer.next();
            if (token.type() == QuotedTokenType.ClosingRoundBracket) {
                return new DataTypeTuple(tupleNestedDataType.toArray(new IDataType[tupleNestedDataType.size()]));
            }

            Validate.isTrue(token.type() == QuotedTokenType.Comma);
        }
    }
}
