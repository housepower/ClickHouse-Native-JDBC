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

package com.github.housepower.jdbc.data.type.complex;

import com.github.housepower.jdbc.ClickHouseArray;
import com.github.housepower.jdbc.connect.NativeContext;
import com.github.housepower.jdbc.data.DataTypeFactory;
import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DataTypeArray implements IDataType {

    public static IDataType createArrayType(SQLLexer lexer, NativeContext.ServerContext serverContext) throws SQLException {
        Validate.isTrue(lexer.character() == '(');
        IDataType arrayNestedType = DataTypeFactory.get(lexer, serverContext);
        Validate.isTrue(lexer.character() == ')');
        return new DataTypeArray("Array(" + arrayNestedType.name() + ")",
                arrayNestedType, DataTypeFactory.get("UInt64", serverContext));
    }

    private final String name;
    private final Array defaultValue;

    public IDataType getElemDataType() {
        return elemDataType;
    }

    private final IDataType elemDataType;
    private final IDataType offsetIDataType;

    public DataTypeArray(String name, IDataType elemDataType, IDataType offsetIDataType) throws SQLException {
        this.name = name;
        this.elemDataType = elemDataType;
        this.offsetIDataType = offsetIDataType;
        this.defaultValue = new ClickHouseArray(new Object[]{elemDataType.defaultValue()});
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int sqlTypeId() {
        return Types.ARRAY;
    }

    @Override
    public Object defaultValue() {
        return defaultValue;
    }

    @Override
    public Class javaTypeClass() {
        return Array.class;
    }

    @Override
    public boolean nullable() {
        return false;
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
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        Validate.isTrue(lexer.character() == '[');
        List<Object> arrayData = new ArrayList<>();
        for (; ; ) {
            if (lexer.isCharacter(']')) {
                lexer.character();
                break;
            }
            if (lexer.isCharacter(',')) {
                lexer.character();
            }
            arrayData.add(elemDataType.deserializeTextQuoted(lexer));
        }
        return new ClickHouseArray(arrayData.toArray());
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        Object []arr = (Object[]) data;
        for (Object f : arr) {
            getElemDataType().serializeBinary(f, serializer);
        }
    }


    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer)
            throws SQLException, IOException {
        offsetIDataType.serializeBinary(data.length, serializer);
        getElemDataType().serializeBinaryBulk(data, serializer);
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        Long offset = (Long) offsetIDataType.deserializeBinary(deserializer);
        return elemDataType.deserializeBinaryBulk(offset.intValue(), deserializer);
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws IOException, SQLException {
        ClickHouseArray[] data = new ClickHouseArray[rows];
        if (rows == 0) {
            return data;
        }

        Object[] offsets = offsetIDataType.deserializeBinaryBulk(rows, deserializer);
        ClickHouseArray res = new ClickHouseArray(
                elemDataType.deserializeBinaryBulk(((BigInteger) offsets[rows - 1]).intValue(), deserializer));

        for (int row = 0, lastOffset = 0; row < rows; row++) {
            BigInteger offset = (BigInteger) offsets[row];
            data[row] = res.slice(lastOffset, offset.intValue() - lastOffset);
            lastOffset = offset.intValue();
        }
        return data;
    }
}
