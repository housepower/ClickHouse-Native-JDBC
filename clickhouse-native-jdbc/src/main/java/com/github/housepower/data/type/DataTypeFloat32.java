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

package com.github.housepower.data.type;

import com.github.housepower.data.IDataType;
import com.github.housepower.misc.SQLLexer;
import com.github.housepower.serde.BinaryDeserializer;
import com.github.housepower.serde.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

public class DataTypeFloat32 implements IDataType<Float, Float> {

    @Override
    public String name() {
        return "Float32";
    }

    @Override
    public int sqlTypeId() {
        return Types.FLOAT;
    }

    @Override
    public Float defaultValue() {
        return 0.0F;
    }

    @Override
    public Class<Float> javaType() {
        return Float.class;
    }

    @Override
    public int getPrecision() {
        return 8;
    }

    @Override
    public int getScale() {
        return 8;
    }

    @Override
    public void serializeBinary(Float data, BinarySerializer serializer) throws SQLException, IOException {
        serializer.writeFloat(data);
    }

    @Override
    public Float deserializeBinary(BinaryDeserializer deserializer) throws IOException {
        return deserializer.readFloat();
    }

    @Override
    public String[] getAliases() {
        return new String[]{"FLOAT"};
    }

    @Override
    public Float deserializeText(SQLLexer lexer) throws SQLException {
        return lexer.numberLiteral().floatValue();
    }

    @Override
    public boolean isSigned() {
        return true;
    }
}
