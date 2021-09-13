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
package com.github.housepower.data.type.complex;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.github.housepower.data.DataTypeFactory;
import com.github.housepower.data.IDataType;
import com.github.housepower.data.type.DataTypeInt64;
import com.github.housepower.misc.SQLLexer;
import com.github.housepower.misc.Validate;
import com.github.housepower.serde.BinaryDeserializer;
import com.github.housepower.serde.BinarySerializer;

/**
 * @author liuxinghua02
 */
public class DataTypeMap implements IDataType<Object, Object> {
    public static DataTypeCreator<Object, Object> creator = (lexer, serverContext) -> {
        Validate.isTrue(lexer.character() == '(');
        IDataType<?, ?> key = DataTypeFactory.get(lexer, serverContext);
        Validate.isTrue(lexer.character() == ',');
        IDataType<?, ?> value = DataTypeFactory.get(lexer, serverContext);
        Validate.isTrue(lexer.character() == ')');

        IDataType<?, ?>[] nestedTypes = new IDataType[]{key, value};
        String name = "Map(" + key.name() + ", " + value.name() + ")";

        return new DataTypeMap(name, nestedTypes, (DataTypeInt64) DataTypeFactory.get("Int64", serverContext));
    };

    private final String name;

    private final IDataType<?, ?>[] nestedTypes;

    private final DataTypeInt64 offsetIDataType;

    public DataTypeMap(String name, IDataType<?, ?>[] nestedTypes, DataTypeInt64 offsetIDataType) {
        this.name = name;
        this.nestedTypes = nestedTypes;
        this.offsetIDataType = offsetIDataType;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<Object> javaType() {
        return Object.class;
    }

    @Override
    public int sqlTypeId() {
        return Types.JAVA_OBJECT;
    }

    @Override
    public int getPrecision() {
        return 0;
    }

    @Override
    public int getScale() {
        return 0;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        if (data instanceof Map) {
            Map<?, ?> dataMap = (Map<?, ?>) data;
            getNestedTypes()[0].serializeBinaryBulk(dataMap.keySet().toArray(), serializer);
            getNestedTypes()[1].serializeBinaryBulk(dataMap.values().toArray(), serializer);
        }
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        offsetIDataType.serializeBinary((long) data.length, serializer);
        for (Object obj : data) {
            if (obj instanceof Map) {
                Map<?, ?> dataMap = (Map<?, ?>) obj;
                getNestedTypes()[0].serializeBinaryBulk(dataMap.keySet().toArray(), serializer);
            }
        }
        for (Object obj : data) {
            if (obj instanceof Map) {
                Map<?, ?> dataMap = (Map<?, ?>) obj;
                getNestedTypes()[1].serializeBinaryBulk(dataMap.values().toArray(), serializer);
            }
        }
    }

    @Override
    public Object deserializeText(SQLLexer lexer) throws SQLException {
        Map<Object, Object> result = new HashMap<>();
        Object key = null;
        Object value = null;
        Validate.isTrue(lexer.character() == '{');
        for (; ; ) {
            if (lexer.isCharacter('}')) {
                lexer.character();
                break;
            }
            key = getNestedTypes()[0].deserializeText(lexer);
            Validate.isTrue(lexer.character() == ':');
            value = getNestedTypes()[1].deserializeText(lexer);
            if (lexer.isCharacter(',')) {
                lexer.character();
            }
            result.put(key, value);
        }

        return result;
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        Map<Object, Object> map = new HashMap<Object, Object>();
        Long offset = offsetIDataType.deserializeBinary(deserializer);
        Object[] keys = getNestedTypes()[0].deserializeBinaryBulk(offset.intValue(), deserializer);
        Object[] values = getNestedTypes()[1].deserializeBinaryBulk(offset.intValue(), deserializer);
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }

        return map;
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws IOException, SQLException {
        Object[] arrays = new Object[rows];
        if (rows == 0) {
            return arrays;
        }

        int[] offsets = Arrays.stream(offsetIDataType.deserializeBinaryBulk(rows, deserializer))
                .mapToInt(value -> ((Long) value).intValue()).toArray();
        Object[] keys = getNestedTypes()[0].deserializeBinaryBulk(offsets[rows - 1], deserializer);
        Object[] values = getNestedTypes()[1].deserializeBinaryBulk(offsets[rows - 1], deserializer);

        Map<Object, Object> map = new HashMap<Object, Object>();
        for (int row = 0, lastOffset = 0; row < rows; row++) {
            int offset = offsets[row];
            for (int i = lastOffset; i < offset; i++) {
                map.put(keys[i], values[i]);
            }
            lastOffset = offset;
            arrays[row] = map;
            map = new HashMap<Object, Object>();
        }
        return arrays;
    }

    public IDataType[] getNestedTypes() {
        return nestedTypes;
    }
}
