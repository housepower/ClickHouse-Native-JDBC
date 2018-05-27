package org.houseflys.jdbc.type.creator.complex;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.ColumnFactory;
import org.houseflys.jdbc.type.ParseResult;
import org.houseflys.jdbc.type.column.complex.NullableColumn;

public class NullableColumnCreator implements ColumnCreator {

    private final String nestedType;
    private final ColumnCreator nestedCreator;

    private final ColumnCreator nullMapCreator = ColumnFactory.getCreator("UInt8");

    public NullableColumnCreator(String nestedType, ColumnCreator nestedCreator) throws SQLException {
        this.nestedType = nestedType;
        this.nestedCreator = nestedCreator;
    }

    @Override
    public Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer)
        throws IOException, SQLException {
        Column nullMap = nullMapCreator.createColumn(rows, "is_null_map", "UInt8", deserializer);

        Column nestedColumn = nestedCreator.createColumn(rows, name, nestedType, deserializer);
        return new NullableColumn(name, type, nullMap, nestedColumn);
    }

    private static final Pattern PATTERN = Pattern.compile("Nullable\\((.+)\\)");

    public static ParseResult parseNullableTypeName(String type, int pos) throws SQLException {
        ParseResult res = ColumnFactory.parseTypeName(type, pos + "Nullable(".length());
        Validate.isTrue(res.pos() < type.length() && type.charAt(res.pos()) == ')', "Unknown data type family:" + type);

        return new ParseResult(
            res.pos() + 1, type.substring(pos, res.pos() + 1), new NullableColumnCreator(res.type(), res.creator()));
    }
}
