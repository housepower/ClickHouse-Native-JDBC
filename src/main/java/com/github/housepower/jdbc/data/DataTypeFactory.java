package com.github.housepower.jdbc.data;

import com.github.housepower.jdbc.data.type.*;
import com.github.housepower.jdbc.data.type.complex.*;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.stream.QuotedLexer;
import com.github.housepower.jdbc.stream.QuotedToken;
import com.github.housepower.jdbc.stream.QuotedTokenType;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class DataTypeFactory {

    public static IDataType get(String type, TimeZone serverZone) throws SQLException {
        QuotedLexer lexer = new QuotedLexer(type);
        IDataType dataType = get(lexer, serverZone);
        Validate.isTrue(lexer.next().type() == QuotedTokenType.EndOfStream);
        return dataType;
    }

    private static final Map<String, IDataType> dataTypes = initialDataTypes();

    public static IDataType get(QuotedLexer lexer, TimeZone serverZone) throws SQLException {
        QuotedToken token = lexer.next();
        Validate.isTrue(token.type() == QuotedTokenType.BareWord);

        if ("Date".equals(token.data())) {
            return DataTypeDate.createDateType(lexer, serverZone);
        } else if ("Tuple".equals(token.data())) {
            return DataTypeTuple.createTupleType(lexer, serverZone);
        } else if ("Array".equals(token.data())) {
            return DataTypeArray.createArrayType(lexer, serverZone);
        } else if ("Enum8".equals(token.data())) {
            return DataTypeEnum8.createEnum8Type(lexer, serverZone);
        } else if ("Enum16".equals(token.data())) {
            return DataTypeEnum16.createEnum16Type(lexer, serverZone);
        } else if ("DateTime".equals(token.data())) {
            return DataTypeDateTime.createDateTimeType(lexer, serverZone);
        } else if ("Nullable".equals(token.data())) {
            return DataTypeNullable.createNullableType(lexer, serverZone);
        } else if ("FixedString".equals(token.data())) {
            return DataTypeFixedString.createFixedStringType(lexer, serverZone);
        } else {
            String typeName = token.data();
            IDataType dataType = dataTypes.get(typeName);
            Validate.isTrue(dataType != null, "Unknown data type family:" + typeName);
            return dataType;
        }
    }

    private static Map<String, IDataType> initialDataTypes() {
        Map<String, IDataType> creators = new HashMap<String, IDataType>();

        creators.put("UUID", new DataTypeUUID());
        creators.put("String", new DataTypeString());
        creators.put("Float32", new DataTypeFloat32());
        creators.put("Float64", new DataTypeFloat64());

        creators.put("Int8", new DataTypeInt8("Int8"));
        creators.put("Int16", new DataTypeInt16("Int16"));
        creators.put("Int32", new DataTypeInt32("Int32"));
        creators.put("Int64", new DataTypeInt64("Int64"));
        creators.put("UInt8", new DataTypeInt8("UInt8"));
        creators.put("UInt16", new DataTypeInt16("UInt16"));
        creators.put("UInt32", new DataTypeInt32("UInt32"));
        creators.put("UInt64", new DataTypeInt64("UInt64"));

        return creators;
    }
}
