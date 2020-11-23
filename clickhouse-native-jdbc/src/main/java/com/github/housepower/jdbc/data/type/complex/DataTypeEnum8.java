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

import com.github.housepower.jdbc.connect.NativeContext;
import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DataTypeEnum8 implements IDataType {

    public static IDataType createEnum8Type(SQLLexer lexer, NativeContext.ServerContext serverContext) throws SQLException {
        Validate.isTrue(lexer.character() == '(');
        List<Byte> enumValues = new ArrayList<>();
        List<String> enumNames = new ArrayList<>();

        for (int i = 0; i < 1 << 8; i++) {
            enumNames.add(lexer.stringLiteral());
            Validate.isTrue(lexer.character() == '=');
            enumValues.add(lexer.numberLiteral().byteValue());

            char character = lexer.character();
            Validate.isTrue(character == ',' || character == ')');

            if (character == ')') {
                StringBuilder builder = new StringBuilder("Enum8(");
                for (int index = 0; index < enumNames.size(); index++) {
                    if (index > 0)
                        builder.append(",");
                    builder.append("'").append(enumNames.get(index)).append("'")
                            .append(" = ").append(enumValues.get(index));
                }
                builder.append(")");
                return new DataTypeEnum8(builder.toString(),
                        enumNames.toArray(new String[0]), enumValues.toArray(new Byte[0]));
            }
        }
        throw new SQLException("DataType Enum8 size must be less than 256");
    }

    private final String name;
    private final Byte[] values;
    private final String[] names;

    public DataTypeEnum8(String name, String[] names, Byte[] values) {
        this.name = name;
        this.names = names;
        this.values = values;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int sqlTypeId() {
        return Types.VARCHAR;
    }

    @Override
    public Object defaultValue() {
        return values[0];
    }

    @Override
    public Class javaTypeClass() {
        return String.class;
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
        return lexer.stringLiteral();
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        for (int i = 0; i < names.length; i++) {
            if (data.equals(names[i])) {
                serializer.writeByte(values[i]);
                return;
            }
        }

        StringBuilder message = new StringBuilder("Expected ");
        for (int i = 0; i < names.length; i++) {
            if (i > 0)
                message.append(" OR ");
            message.append(names[i]);
        }
        message.append(", but was ").append(data);

        throw new SQLException(message.toString());
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        byte value = deserializer.readByte();
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                return names[i];
            }
        }
        throw new SQLException("");
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        String[] data = new String[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = (String) deserializeBinary(deserializer);
        }
        return data;
    }
}
