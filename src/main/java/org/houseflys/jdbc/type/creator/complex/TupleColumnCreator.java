package org.houseflys.jdbc.type.creator.complex;

import org.houseflys.jdbc.misc.KeywordScanner;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.ColumnFactory;
import org.houseflys.jdbc.type.ParseResult;
import org.houseflys.jdbc.type.column.complex.TupleColumn;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

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

    private Object[][] getRowsWithElems(int rows, BinaryDeserializer deserializer)
        throws IOException, SQLException {
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

            //Skip the spaces
            index = KeywordScanner.skipSpace(index, type);
            int next = KeywordScanner.scanTo(index, ',', type);
            if (next != -1) {
                ParseResult res = ColumnFactory.parseTypeName(type.substring(index, next), 0);
                nestedCreators.add(res.creator());
                index = next + 1;
            } else {
                next = KeywordScanner.scanTo(index, ')', type);
                ParseResult res = ColumnFactory.parseTypeName(type.substring(index, next), 0);
                nestedCreators.add(res.creator());

                return new ParseResult(
                    next + 1, type.substring(pos, next + 1),
                    new TupleColumnCreator(
                        nestedCreators.toArray(new ColumnCreator[nestedCreators.size()]))
                );
            }
        }
        throw new SQLException("Unknown data type family:" + type);
    }

}
