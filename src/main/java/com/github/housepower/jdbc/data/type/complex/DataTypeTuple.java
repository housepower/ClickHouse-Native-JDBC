package com.github.housepower.jdbc.data.type.complex;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.github.housepower.jdbc.ClickHouseStruct;
import com.github.housepower.jdbc.data.DataTypeFactory;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;
import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.stream.QuotedLexer;
import com.github.housepower.jdbc.stream.QuotedToken;
import com.github.housepower.jdbc.stream.QuotedTokenType;

public class DataTypeTuple implements IDataType {

    private final String name;
    private final IDataType[] elemsDataType;

    public DataTypeTuple(String name, IDataType[] elemsDataType) {
        this.name = name;
        this.elemsDataType = elemsDataType;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int sqlTypeId() {
        return Types.STRUCT;
    }

    @Override
    public Object defaultValue() {
        Object[] attrs = new Object[elemsDataType.length];
        for (int i = 0; i < elemsDataType.length; i++) {
            attrs[i] = elemsDataType[i].defaultValue();
        }
        return new ClickHouseStruct("Tuple", attrs);
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        Validate.isTrue(data instanceof Struct && "Tuple".equals(((Struct) data).getSQLTypeName()),
            "Expected Struct Parameter, but was " + data.getClass().getSimpleName());

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
        return new ClickHouseStruct("Tuple", attrs);
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        for (int i = 0; i < elemsDataType.length; i++) {
            Object[] elemsData = new Object[data.length];
            for (int row = 0; row < data.length; row++) {
                Validate.isTrue(data[row] instanceof Struct && "Tuple".equals(((Struct) data[row]).getSQLTypeName()),
                    "Expected Struct Parameter, but was " + data.getClass().getSimpleName());
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
            rowsData[row] = new ClickHouseStruct("Tuple", elemsData);
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
        return new ClickHouseStruct("Tuple", elems);
    }

    public static DataTypeTuple createTupleType(QuotedLexer lexer) throws SQLException {
        Validate.isTrue(lexer.next().type() == QuotedTokenType.OpeningRoundBracket);
        List<IDataType> tupleNestedDataType = new ArrayList<IDataType>();

        for (; ; ) {
            tupleNestedDataType.add(DataTypeFactory.get(lexer));
            QuotedToken token = lexer.next();
            if (token.type() == QuotedTokenType.ClosingRoundBracket) {
                StringBuilder builder = new StringBuilder("Tuple(");
                IDataType[] types = new IDataType[tupleNestedDataType.size()];
                for (int i = 0; i < tupleNestedDataType.size(); i++) {
                    types[i] = tupleNestedDataType.get(i);
                    builder.append(tupleNestedDataType.get(i).name());
                    if (i == tupleNestedDataType.size() - 1) {
                        return new DataTypeTuple(builder.append(")").toString(), types);
                    }

                    builder.append(" ,");
                }
            }

            Validate.isTrue(token.type() == QuotedTokenType.Comma);
        }
    }
}
