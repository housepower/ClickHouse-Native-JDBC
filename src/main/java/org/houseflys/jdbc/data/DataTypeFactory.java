package org.houseflys.jdbc.data;

import org.houseflys.jdbc.data.type.complex.*;
import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.data.type.DataTypeDate;
import org.houseflys.jdbc.data.type.DataTypeFloat32;
import org.houseflys.jdbc.data.type.DataTypeFloat64;
import org.houseflys.jdbc.data.type.DataTypeInt16;
import org.houseflys.jdbc.data.type.DataTypeInt32;
import org.houseflys.jdbc.data.type.DataTypeInt64;
import org.houseflys.jdbc.data.type.DataTypeInt8;
import org.houseflys.jdbc.data.type.DataTypeString;
import org.houseflys.jdbc.data.type.DataTypeUUID;
import org.houseflys.jdbc.stream.QuotedLexer;
import org.houseflys.jdbc.stream.QuotedToken;
import org.houseflys.jdbc.stream.QuotedTokenType;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class DataTypeFactory {

    public static IDataType get(String type) throws SQLException {
        QuotedLexer lexer = new QuotedLexer(type);
        IDataType dataType = get(lexer);
        Validate.isTrue(lexer.next().type() == QuotedTokenType.EndOfStream);
        return dataType;
    }

    private static final Map<String, IDataType> dataTypes = dataTypes();
    private static final Map<String, IDataType> dataTypesWithCaseInsensitive = ignoreCaseDataTypes();


    public static IDataType get(QuotedLexer lexer) throws SQLException {
        QuotedToken token = lexer.next();
        Validate.isTrue(token.type() == QuotedTokenType.BareWord);

        if ("Tuple".equals(token.data())) {
            return DataTypeTuple.createTupleType(lexer);
        } else if ("Array".equals(token.data())) {
            return DataTypeArray.createArrayType(lexer);
        } else if ("Enum8".equals(token.data())) {
            return DataTypeEnum8.createEnum8Type(lexer);
        } else if ("Enum16".equals(token.data())) {
            return DataTypeEnum16.createEnum16Type(lexer);
        } else if ("DateTime".equals(token.data())) {
            return DataTypeDateTime.createDateTimeType(lexer);
        } else if ("Nullable".equals(token.data())) {
            return DataTypeNullable.createNullableType(lexer);
        } else if ("FixedString".equals(token.data())) {
            return DataTypeFixedString.createFixedStringType(lexer);
        } else {
            String typeName = token.data();
            IDataType dataType = dataTypes.containsKey(typeName) ?
                dataTypes.get(typeName) : dataTypesWithCaseInsensitive.get(typeName);

            Validate.isTrue(dataType != null, "Unknown data type family:" + typeName);
            return dataType;
        }
    }

    private static Map<String, IDataType> dataTypes() {
        Map<String, IDataType> creators = new HashMap<String, IDataType>();

        creators.put("Date", new DataTypeDate());
        creators.put("Int8", new DataTypeInt8());
        creators.put("Int16", new DataTypeInt16());
        creators.put("Int32", new DataTypeInt32());
        creators.put("Int64", new DataTypeInt64());
        creators.put("UInt8", new DataTypeInt8());
        creators.put("UInt16", new DataTypeInt16());
        creators.put("UInt32", new DataTypeInt32());
        creators.put("UInt64", new DataTypeInt64());
        creators.put("String", new DataTypeString());
        creators.put("Enum8", new DataTypeString());
        creators.put("Enum16", new DataTypeString());
        creators.put("Float32", new DataTypeFloat32());
        creators.put("Float64", new DataTypeFloat64());
        creators.put("DateTime", new DataTypeDateTime(TimeZone.getDefault()));
        creators.put("UUID", new DataTypeUUID());

        return creators;
    }

    private static Map<String, IDataType> ignoreCaseDataTypes() {
        Map<String, IDataType> creatorsWithCaseInsensitive = new HashMap<String, IDataType>();

        creatorsWithCaseInsensitive.put("CHAR", new DataTypeString());
        creatorsWithCaseInsensitive.put("VARCHAR", new DataTypeString());
        creatorsWithCaseInsensitive.put("TEXT", new DataTypeString());
        creatorsWithCaseInsensitive.put("TINYTEXT", new DataTypeString());
        creatorsWithCaseInsensitive.put("MEDIUMTEXT", new DataTypeString());
        creatorsWithCaseInsensitive.put("LONGTEXT", new DataTypeString());
        creatorsWithCaseInsensitive.put("BLOB", new DataTypeString());
        creatorsWithCaseInsensitive.put("TINYBLOB", new DataTypeString());
        creatorsWithCaseInsensitive.put("MEDIUMBLOB", new DataTypeString());
        creatorsWithCaseInsensitive.put("LONGBLOB", new DataTypeString());

        creatorsWithCaseInsensitive.put("TINYINT", new DataTypeInt8());
        creatorsWithCaseInsensitive.put("SMALLINT", new DataTypeInt16());
        creatorsWithCaseInsensitive.put("INT", new DataTypeInt32());
        creatorsWithCaseInsensitive.put("INTEGER", new DataTypeInt32());
        creatorsWithCaseInsensitive.put("BIGINT", new DataTypeInt64());
        creatorsWithCaseInsensitive.put("FLOAT", new DataTypeFloat32());
        creatorsWithCaseInsensitive.put("DOUBLE", new DataTypeFloat64());

        return creatorsWithCaseInsensitive;
    }
}
