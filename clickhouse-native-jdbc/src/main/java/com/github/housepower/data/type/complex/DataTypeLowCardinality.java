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

import com.github.housepower.data.DataTypeFactory;
import com.github.housepower.data.IDataType;
import com.github.housepower.misc.SQLLexer;
import com.github.housepower.misc.Validate;
import com.github.housepower.serde.BinaryDeserializer;
import com.github.housepower.serde.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;

public class DataTypeLowCardinality implements IDataType {

    public static DataTypeCreator creator = (lexer, serverContext) -> {
        Validate.isTrue(lexer.character() == '(');
        IDataType nestedType = DataTypeFactory.get(lexer, serverContext);
        Validate.isTrue(lexer.character() == ')');
        return new DataTypeLowCardinality(
                "LowCardinality(" + nestedType.name() + ")", nestedType);
    };

    private final String name;
    private final IDataType nestedDataType;

    public DataTypeLowCardinality(String name, IDataType nestedDataType) {
        this.name = name;
        this.nestedDataType = nestedDataType;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public int sqlTypeId() {
        return this.nestedDataType.sqlTypeId();
    }

    @Override
    public Object defaultValue() {
        return this.nestedDataType.defaultValue();
    }

    @Override
    public Class javaType() {
        return this.nestedDataType.javaType();
    }

    @Override
    public Class jdbcJavaType() {
        return this.nestedDataType.jdbcJavaType();
    }

    @Override
    public boolean nullable() {
        return this.nestedDataType.nullable();
    }

    @Override
    public int getPrecision() {
        return this.nestedDataType.getPrecision();
    }

    @Override
    public int getScale() {
        return this.nestedDataType.getScale();
    }

    @Override
    public Object deserializeText(SQLLexer lexer) throws SQLException {
        return this.nestedDataType.deserializeText(lexer);
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        this.nestedDataType.serializeBinary(data, serializer);
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        this.nestedDataType.serializeBinaryBulk(data, serializer);
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        return this.nestedDataType.deserializeBinary(deserializer);
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        Object[] data = this.nestedDataType.deserializeBinaryBulk(rows, deserializer);
        return data;
    }

    @Override
    public boolean isSigned() {
        return this.nestedDataType.isSigned();
    }
}
