package org.houseflys.jdbc.type;

import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.creator.DateColumnCreator;
import org.houseflys.jdbc.type.creator.complex.EnumColumnCreator;
import org.houseflys.jdbc.type.creator.Float32ColumnCreator;
import org.houseflys.jdbc.type.creator.Float64ColumnCreator;
import org.houseflys.jdbc.type.creator.Int16ColumnCreator;
import org.houseflys.jdbc.type.creator.Int32ColumnCreator;
import org.houseflys.jdbc.type.creator.Int64ColumnCreator;
import org.houseflys.jdbc.type.creator.Int8ColumnCreator;
import org.houseflys.jdbc.type.creator.StringColumnCreator;
import org.houseflys.jdbc.type.creator.complex.ArrayColumnCreator;
import org.houseflys.jdbc.type.creator.complex.DateTimeColumnCreator;
import org.houseflys.jdbc.type.creator.complex.FixedStringColumnCreator;
import org.houseflys.jdbc.type.creator.complex.NullableColumnCreator;
import org.houseflys.jdbc.type.creator.complex.TupleColumnCreator;
import org.houseflys.jdbc.type.creator.UUIDColumnCreator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColumnFactory {

    public static Column createColumn(int rows, BinaryDeserializer deserializer) throws IOException, SQLException {
        String name = deserializer.readStringBinary();
        String type = deserializer.readStringBinary();

        return getCreator(type).createColumn(rows, name, type, deserializer);
    }

    public static ColumnCreator getCreator(String type) throws SQLException {
        ParseResult res = parseTypeName(type, 0);
        Validate.isTrue(res.pos() == type.length(), "Unknown data type family:" + type);
        return res.creator();
    }

    private static final Pattern SIMPLE_TYPE_REGEX = Pattern.compile("([\\_|A-Z|a-z][\\_|A-Z|a-z|0-9]*)");
    private static final Map<String, ColumnCreator> creators = initialCreators();
    private static final Map<String, ColumnCreator> creatorsWithCaseInsensitive = initialCreatorsWithCaseInsensitive();


    public static ParseResult parseTypeName(String type, int pos) throws SQLException {
        Matcher matcher = SIMPLE_TYPE_REGEX.matcher(type);
        return matcher.find(pos) && matcher.start() == pos &&
                   (matcher.end() == type.length() || type.charAt(matcher.end()) != '(') ?
                   parseSimpleTypeName(matcher.group(1), matcher.end()) : parseComplexTypeName(type, pos);
    }

    private static ParseResult parseSimpleTypeName(String type, int end) throws SQLException {
        ColumnCreator creator =
            creators.containsKey(type) ? creators.get(type) : creatorsWithCaseInsensitive.get(type.toUpperCase());

        Validate.isTrue(creator != null, "Unknown data type family:" + type);
        return new ParseResult(end, type, creator);
    }

    private static ParseResult parseComplexTypeName(String type, int pos) throws SQLException {
        if (type.startsWith("Tuple", pos)) {
            return TupleColumnCreator.parseTupleTypeName(type, pos);
        } else if (type.startsWith("Array", pos)) {
            return ArrayColumnCreator.parseArrayTypeName(type, pos);
        } else if (type.startsWith("DateTime", pos)) {
            return DateTimeColumnCreator.parseDateTimeTypeName(type, pos);
        } else if (type.startsWith("Nullable", pos)) {
            return NullableColumnCreator.parseNullableTypeName(type, pos);
        } else if (type.startsWith("FixedString", pos)) {
            return FixedStringColumnCreator.parseFixedStringTypeName(type, pos);
        } else if (type.startsWith("Enum", pos)) {
            return EnumColumnCreator.parseEnumTypeName(type, pos);
        }
        throw new SQLException("Unknown data type family:" + type);
    }


    private static Map<String, ColumnCreator> initialCreators() {
        Map<String, ColumnCreator> creators = new HashMap<String, ColumnCreator>();

        creators.put("Date", new DateColumnCreator());
        creators.put("Int8", new Int8ColumnCreator());
        creators.put("Int16", new Int16ColumnCreator());
        creators.put("Int32", new Int32ColumnCreator());
        creators.put("Int64", new Int64ColumnCreator());
        creators.put("UInt8", new Int8ColumnCreator());
        creators.put("UInt16", new Int16ColumnCreator());
        creators.put("UInt32", new Int32ColumnCreator());
        creators.put("UInt64", new Int64ColumnCreator());
        creators.put("String", new StringColumnCreator());
        creators.put("Float32", new Float32ColumnCreator());
        creators.put("Float64", new Float64ColumnCreator());
        creators.put("DateTime", new DateTimeColumnCreator(TimeZone.getDefault()));
        creators.put("UUID", new UUIDColumnCreator());

        return creators;
    }

    private static Map<String, ColumnCreator> initialCreatorsWithCaseInsensitive() {
        Map<String, ColumnCreator> creatorsWithCaseInsensitive = new HashMap<String, ColumnCreator>();

        creatorsWithCaseInsensitive.put("CHAR", new StringColumnCreator());
        creatorsWithCaseInsensitive.put("VARCHAR", new StringColumnCreator());
        creatorsWithCaseInsensitive.put("TEXT", new StringColumnCreator());
        creatorsWithCaseInsensitive.put("TINYTEXT", new StringColumnCreator());
        creatorsWithCaseInsensitive.put("MEDIUMTEXT", new StringColumnCreator());
        creatorsWithCaseInsensitive.put("LONGTEXT", new StringColumnCreator());
        creatorsWithCaseInsensitive.put("BLOB", new StringColumnCreator());
        creatorsWithCaseInsensitive.put("TINYBLOB", new StringColumnCreator());
        creatorsWithCaseInsensitive.put("MEDIUMBLOB", new StringColumnCreator());
        creatorsWithCaseInsensitive.put("LONGBLOB", new StringColumnCreator());

        creatorsWithCaseInsensitive.put("TINYINT", new Int8ColumnCreator());
        creatorsWithCaseInsensitive.put("SMALLINT", new Int16ColumnCreator());
        creatorsWithCaseInsensitive.put("INT", new Int32ColumnCreator());
        creatorsWithCaseInsensitive.put("INTEGER", new Int32ColumnCreator());
        creatorsWithCaseInsensitive.put("BIGINT", new Int64ColumnCreator());
        creatorsWithCaseInsensitive.put("FLOAT", new Float32ColumnCreator());
        creatorsWithCaseInsensitive.put("DOUBLE", new Float64ColumnCreator());

        return creatorsWithCaseInsensitive;
    }

}
