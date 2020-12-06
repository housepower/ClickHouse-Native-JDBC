/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.housepower.jdbc.data;

import com.github.housepower.jdbc.connect.NativeContext;
import com.github.housepower.jdbc.data.type.*;
import com.github.housepower.jdbc.data.type.complex.*;
import com.github.housepower.jdbc.misc.LRUCache;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.StringView;
import com.github.housepower.jdbc.misc.Validate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class DataTypeFactory {
    private static final LRUCache<String, IDataType> DATA_TYPE_CACHE = new LRUCache<>(1024);

    public static IDataType get(String type, NativeContext.ServerContext serverContext) throws SQLException {
        IDataType dataType = DATA_TYPE_CACHE.get(type);
        if (dataType != null) {
            DATA_TYPE_CACHE.put(type, dataType);
            return dataType;
        }

        SQLLexer lexer = new SQLLexer(0, type);
        dataType = get(lexer, serverContext);
        Validate.isTrue(lexer.eof());

        DATA_TYPE_CACHE.put(type, dataType);
        return dataType;
    }

    private static final Map<String, IDataType> dataTypes = initialDataTypes();

    public static IDataType get(SQLLexer lexer, NativeContext.ServerContext serverContext) throws SQLException {
        StringView dataTypeName = lexer.bareWord();

        if (dataTypeName.checkEquals("Date") || "Date".equalsIgnoreCase(String.valueOf(dataTypeName))) {
            return DataTypeDate.createDateType(lexer, serverContext);
        } else if (dataTypeName.checkEquals("Tuple") || "Tuple".equalsIgnoreCase(String.valueOf(dataTypeName))) {
            return DataTypeTuple.createTupleType(lexer, serverContext);
        } else if (dataTypeName.checkEquals("Array") || "Array".equalsIgnoreCase(String.valueOf(dataTypeName))) {
            return DataTypeArray.createArrayType(lexer, serverContext);
        } else if (dataTypeName.checkEquals("Enum8") || "Enum8".equalsIgnoreCase(String.valueOf(dataTypeName))) {
            return DataTypeEnum8.createEnum8Type(lexer, serverContext);
        } else if (dataTypeName.checkEquals("Enum16") || "Enum16".equalsIgnoreCase(String.valueOf(dataTypeName))) {
            return DataTypeEnum16.createEnum16Type(lexer, serverContext);
        } else if (dataTypeName.checkEquals("DateTime") || "DateTime".equalsIgnoreCase(String.valueOf(dataTypeName))) {
            return DataTypeDateTime.createDateTimeType(lexer, serverContext);
        } else if (dataTypeName.checkEquals("DateTime64") || "DateTime64".equalsIgnoreCase(String.valueOf(dataTypeName))) {
            return DataTypeDateTime64.createDateTime64Type(lexer, serverContext);
        } else if (dataTypeName.checkEquals("Nullable") || "Nullable".equalsIgnoreCase(String.valueOf(dataTypeName))) {
            return DataTypeNullable.createNullableType(lexer, serverContext);
        } else if (dataTypeName.checkEquals("FixedString") || "FixedString".equalsIgnoreCase(String.valueOf(dataTypeName))) {
            return DataTypeFixedString.createFixedStringType(lexer, serverContext);
        } else if (dataTypeName.checkEquals("Decimal") || "Decimal".equalsIgnoreCase(String.valueOf(dataTypeName))) {
            return DataTypeDecimal.createDecimalType(lexer, serverContext);
        } else if (dataTypeName.checkEquals("String") || "String".equalsIgnoreCase(String.valueOf(dataTypeName))) {
            return new DataTypeString(serverContext);
        } else {
            String name = String.valueOf(dataTypeName).toLowerCase(Locale.ROOT);
            IDataType dataType = dataTypes.get(name);
            Validate.isTrue(dataType != null, "Unknown data type: " + name);
            return dataType;
        }
    }

    /**
     * Some framework like Spark JDBC will convert all types name to lower case
     */
    private static Map<String, IDataType> initialDataTypes() {
        Map<String, IDataType> creators = new HashMap<>();

        creators.put("ipv4", new DataTypeIPv4());
        creators.put("uuid", new DataTypeUUID());
        creators.put("float32", new DataTypeFloat32());
        creators.put("float64", new DataTypeFloat64());

        creators.put("int8", new DataTypeInt8("Int8"));
        creators.put("int16", new DataTypeInt16("Int16"));
        creators.put("int32", new DataTypeInt32("Int32"));
        creators.put("int64", new DataTypeInt64("Int64"));
        creators.put("uint8", new DataTypeInt8("UInt8"));
        creators.put("uint16", new DataTypeInt16("UInt16"));
        creators.put("uint32", new DataTypeInt32("UInt32"));
        creators.put("uint64", new DataTypeInt64("UInt64"));

        return creators;
    }
}
