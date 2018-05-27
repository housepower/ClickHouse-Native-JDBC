package org.houseflys.jdbc.type.creator.complex;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.ParseResult;
import org.houseflys.jdbc.type.column.complex.FixedStringColumn;

public class FixedStringColumnCreator implements ColumnCreator {
    private final int n;

    public FixedStringColumnCreator(int n) {
        this.n = n;
    }

    @Override
    public Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer)
        throws IOException, SQLException {
        String[] data = new String[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = new String(deserializer.readBytes(n));
        }
        return new FixedStringColumn(name, type, data);
    }

    private static final Pattern REGEX = Pattern.compile("(FixedString\\((\\d+)\\))");

    public static ParseResult parseFixedStringTypeName(String type, int pos) throws SQLException {
        Matcher matcher = REGEX.matcher(type);
        Validate.isTrue(matcher.find(pos) && matcher.start() == pos, "Unknown data type family:" + type);
        return new ParseResult(matcher.end(), matcher.group(1),
            new FixedStringColumnCreator(Integer.parseInt(matcher.group(2))));
    }
}
