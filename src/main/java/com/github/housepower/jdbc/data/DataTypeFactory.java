package com.github.housepower.jdbc.data;

import com.github.housepower.jdbc.connect.PhysicalInfo;
import com.github.housepower.jdbc.data.type.DataTypeDate;
import com.github.housepower.jdbc.data.type.DataTypeFloat32;
import com.github.housepower.jdbc.data.type.DataTypeFloat64;
import com.github.housepower.jdbc.data.type.DataTypeInt16;
import com.github.housepower.jdbc.data.type.DataTypeInt32;
import com.github.housepower.jdbc.data.type.DataTypeInt64;
import com.github.housepower.jdbc.data.type.DataTypeInt8;
import com.github.housepower.jdbc.data.type.DataTypeString;
import com.github.housepower.jdbc.data.type.DataTypeUUID;
import com.github.housepower.jdbc.data.type.complex.DataTypeArray;
import com.github.housepower.jdbc.data.type.complex.DataTypeDateTime;
import com.github.housepower.jdbc.data.type.complex.DataTypeEnum16;
import com.github.housepower.jdbc.data.type.complex.DataTypeEnum8;
import com.github.housepower.jdbc.data.type.complex.DataTypeFixedString;
import com.github.housepower.jdbc.data.type.complex.DataTypeNullable;
import com.github.housepower.jdbc.data.type.complex.DataTypeTuple;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.StringView;
import com.github.housepower.jdbc.misc.Validate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class DataTypeFactory {

    public static IDataType get(String type, PhysicalInfo.ServerInfo serverInfo) throws SQLException {
        SQLLexer lexer = new SQLLexer(0, type);
        IDataType dataType = get(lexer, serverInfo);
        Validate.isTrue(lexer.eof());
        return dataType;
    }

    private static final Map<String, IDataType> dataTypes = initialDataTypes();

    public static IDataType get(SQLLexer lexer, PhysicalInfo.ServerInfo serverInfo) throws SQLException {
        StringView dataTypeName = lexer.bareWord();
        
        if (dataTypeName.equals("Date")) {
            return DataTypeDate.createDateType(lexer, serverInfo);
        } else if (dataTypeName.equals("Tuple")) {
            return DataTypeTuple.createTupleType(lexer, serverInfo);
        } else if (dataTypeName.equals("Array")) {
            return DataTypeArray.createArrayType(lexer, serverInfo);
        } else if (dataTypeName.equals("Enum8")) {
            return DataTypeEnum8.createEnum8Type(lexer, serverInfo);
        } else if (dataTypeName.equals("Enum16")) {
            return DataTypeEnum16.createEnum16Type(lexer, serverInfo);
        } else if (dataTypeName.equals("DateTime")) {
            return DataTypeDateTime.createDateTimeType(lexer, serverInfo);
        } else if (dataTypeName.equals("Nullable")) {
            return DataTypeNullable.createNullableType(lexer, serverInfo);
        } else if (dataTypeName.equals("FixedString")) {
            return DataTypeFixedString.createFixedStringType(lexer, serverInfo);
        } else {
            String name = String.valueOf(dataTypeName);
            IDataType dataType = dataTypes.get(name);
            Validate.isTrue(dataType != null, "Unknown data type family:" + name);
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
