package org.houseflys.jdbc.type.creator.complex;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.ColumnFactory;
import org.houseflys.jdbc.type.ParseResult;
import org.houseflys.jdbc.type.column.complex.ArrayColumn;
import org.houseflys.jdbc.type.column.complex.ArrayColumn.ClickHouseArray;

public class ArrayColumnCreator implements ColumnCreator {
    private final String nestedType;
    private final ColumnCreator nestedCreator;

    public ArrayColumnCreator(String nestedType, ColumnCreator nestedCreator) throws SQLException {
        this.nestedType = nestedType;
        this.nestedCreator = nestedCreator;
    }

    @Override
    public Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer)
        throws IOException, SQLException {
        ClickHouseArray[] data = new ClickHouseArray[rows];

        Column offsets = ColumnFactory.getCreator("UInt64").createColumn(rows, "array_offsets", "UInt64", deserializer);
        for (int row = 0, lastOffset = 0; row < rows; row++) {
            Long offset = (Long) offsets.data(row);
            data[row] = new ClickHouseArray(
                offset.intValue() - lastOffset,
                nestedCreator.createColumn(offset.intValue() - lastOffset, "array_elements", nestedType, deserializer));
            lastOffset = offset.intValue();
        }
        return new ArrayColumn(name, type, offsets, data);
    }
    
    public static ParseResult parseArrayTypeName(String type, int pos) throws SQLException {
        ParseResult res = ColumnFactory.parseTypeName(type, pos + "Array(".length());
        Validate.isTrue(res.pos() < type.length() && type.charAt(res.pos()) == ')', "Unknown data type family:" + type);

        return new ParseResult(
            res.pos() + 1, type.substring(pos, res.pos() + 1), new ArrayColumnCreator(res.type(), res.creator()));
    }
}
