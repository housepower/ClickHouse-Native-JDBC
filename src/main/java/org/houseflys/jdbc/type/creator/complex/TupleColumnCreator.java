package org.houseflys.jdbc.type.creator.complex;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.ColumnFactory;
import org.houseflys.jdbc.type.ParseResult;
import org.houseflys.jdbc.type.column.complex.TupleColumn;

public class TupleColumnCreator implements ColumnCreator {

    private final ColumnCreator[] creators;

    public TupleColumnCreator(ColumnCreator[] creators) {
        this.creators = creators;
    }

    @Override
    public Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer)
        throws IOException, SQLException {
        Object[][] rowsWithElems = getRowsWithElems(rows, deserializer);

        Struct[] rowsData = new Struct[rows];
        for (int row = 0; row < rows; row++) {
            Object[] elements = rowsWithElems[row];
            rowsData[row] = new TupleColumn.ClickHouseStruct(elements);
        }
        return new TupleColumn(name, type, rowsData);
    }

    private Object[][] getRowsWithElems(int rows, BinaryDeserializer deserializer) throws IOException, SQLException {
        Object[][] rowsWithElems = new Object[rows][creators.length];
        for (int index = 0; index < creators.length; index++) {
            Column eleColumn = creators[index].createColumn(rows, "tuple_ele", "", deserializer);
            for (int row = 0; row < rows; row++) {
                rowsWithElems[row][index] = eleColumn.data(row);
            }
        }
        return rowsWithElems;
    }

    public static ParseResult parseTupleTypeName(String type, int pos) throws SQLException {
        List<ColumnCreator> nestedCreators = new ArrayList<ColumnCreator>();
        for (int begin = pos + "Tuple(".length(), index = begin; index < type.length(); ) {
            if (type.charAt(index) == ')') {
                Validate.isTrue(index != begin, "Unknown data type family:" + type);
                return new ParseResult(
                    index + 1, type.substring(pos, index + 1),
                    new TupleColumnCreator(nestedCreators.toArray(new ColumnCreator[nestedCreators.size()]))
                );
            }

            if (index != begin) {
                boolean validated = false;
                for (int i = index; i < type.length(); i++) {
                    if (!isWhitespace(type.charAt(i))) {
                        if (!validated) {
                            Validate.isTrue(type.charAt(index) == ',', "Unknown data type family:" + type);
                            validated = true;
                        } else {
                            index = i;
                            break;
                        }
                    }
                }
            }

            ParseResult res = ColumnFactory.parseTypeName(type, index);
            nestedCreators.add(res.creator());
            index = res.pos();
        }
        throw new SQLException("Unknown data type family:" + type);
    }

    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f';
    }
}
