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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.github.housepower.jdbc.connect.NativeContext;
import com.github.housepower.jdbc.data.type.DataTypeDate;
import com.github.housepower.jdbc.data.type.DataTypeFloat32;
import com.github.housepower.jdbc.data.type.DataTypeFloat64;
import com.github.housepower.jdbc.data.type.DataTypeIPv4;
import com.github.housepower.jdbc.data.type.DataTypeInt16;
import com.github.housepower.jdbc.data.type.DataTypeInt32;
import com.github.housepower.jdbc.data.type.DataTypeInt64;
import com.github.housepower.jdbc.data.type.DataTypeInt8;
import com.github.housepower.jdbc.data.type.DataTypeUUID;
import com.github.housepower.jdbc.data.type.complex.DataTypeArray;
import com.github.housepower.jdbc.data.type.complex.DataTypeCreator;
import com.github.housepower.jdbc.data.type.complex.DataTypeDateTime;
import com.github.housepower.jdbc.data.type.complex.DataTypeDateTime64;
import com.github.housepower.jdbc.data.type.complex.DataTypeDecimal;
import com.github.housepower.jdbc.data.type.complex.DataTypeEnum16;
import com.github.housepower.jdbc.data.type.complex.DataTypeEnum8;
import com.github.housepower.jdbc.data.type.complex.DataTypeFixedString;
import com.github.housepower.jdbc.data.type.complex.DataTypeNothing;
import com.github.housepower.jdbc.data.type.complex.DataTypeNullable;
import com.github.housepower.jdbc.data.type.complex.DataTypeString;
import com.github.housepower.jdbc.data.type.complex.DataTypeTuple;
import com.github.housepower.jdbc.misc.LRUCache;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;


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
        String dataTypeName = String.valueOf(lexer.bareWord());

        if (dataTypeName.equalsIgnoreCase("Tuple")) {
            return DataTypeTuple.creator.createDataType(lexer, serverContext);
        } else if (dataTypeName.equalsIgnoreCase("Array")) {
            return DataTypeArray.creator.createDataType(lexer, serverContext);
        } else if (dataTypeName.equalsIgnoreCase("Enum8")) {
            return DataTypeEnum8.creator.createDataType(lexer, serverContext);
        } else if (dataTypeName.equalsIgnoreCase("Enum16")) {
            return DataTypeEnum16.creator.createDataType(lexer, serverContext);
        } else if (dataTypeName.equalsIgnoreCase("DateTime")) {
            return DataTypeDateTime.creator.createDataType(lexer, serverContext);
        } else if (dataTypeName.equalsIgnoreCase("DateTime64")) {
            return DataTypeDateTime64.creator.createDataType(lexer, serverContext);
        } else if (dataTypeName.equalsIgnoreCase("Nullable")) {
            return DataTypeNullable.creator.createDataType(lexer, serverContext);
        } else if (dataTypeName.equalsIgnoreCase("FixedString") || dataTypeName.equals("Binary")) {
            return DataTypeFixedString.creator.createDataType(lexer, serverContext);
        } else if (dataTypeName.equalsIgnoreCase("Decimal")) {
            return DataTypeDecimal.creator.createDataType(lexer, serverContext);
        } else if (dataTypeName.equalsIgnoreCase("String")) {
            return DataTypeString.CREATOR.createDataType(lexer, serverContext);
        } else if (dataTypeName.equalsIgnoreCase("Nothing")) {
            return DataTypeNothing.CREATOR.createDataType(lexer, serverContext);
        } else {
            IDataType dataType = dataTypes.get(dataTypeName.toLowerCase(Locale.ROOT));
            Validate.isTrue(dataType != null, "Unknown data type: " + dataTypeName);
            return dataType;
        }
    }

    /**
     * Some framework like Spark JDBC will convert all types name to lower case
     */
    private static Map<String, IDataType> initialDataTypes() {
        Map<String, IDataType> creators = new HashMap<>();

        registerType(creators, new DataTypeIPv4());
        registerType(creators, new DataTypeUUID());
        registerType(creators, new DataTypeFloat32());
        registerType(creators, new DataTypeFloat64());

        registerType(creators, new DataTypeInt8("Int8"));
        registerType(creators, new DataTypeInt16("Int16"));
        registerType(creators, new DataTypeInt32("Int32"));
        registerType(creators, new DataTypeInt64("Int64"));

        registerType(creators, new DataTypeInt8("UInt8"));
        registerType(creators, new DataTypeInt16("UInt16"));
        registerType(creators, new DataTypeInt32("UInt32"));
        registerType(creators, new DataTypeInt64("UInt64"));

        registerType(creators, new DataTypeDate());
        return creators;
    }

    private static void registerType(Map<String, IDataType> creators, IDataType type) {
        creators.put(type.name().toLowerCase(Locale.ROOT), type);
        for (String typeName : type.getAliases()) {
            creators.put(typeName.toLowerCase(Locale.ROOT), type);
        }
    }

    // TODO
    private static Map<String, DataTypeCreator> initComplexDataTypes() {
        Map<String, DataTypeCreator> creators = new HashMap<>();
        return creators;
    }

    private static void registerComplexType(Map<String, DataTypeCreator> creators, IDataType type, DataTypeCreator creator) {
        creators.put(type.name().toLowerCase(Locale.ROOT), creator);
        for (String typeName : type.getAliases()) {
            creators.put(typeName.toLowerCase(Locale.ROOT), creator);
        }
    }
}
