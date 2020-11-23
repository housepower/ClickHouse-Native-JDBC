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

package com.github.housepower.jdbc.data.type;

import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

public class DataTypeInt8 implements IDataType {

    private static final Byte DEFAULT_VALUE = 0;
    private final String name;
    private final boolean isUnsigned;

    public DataTypeInt8(String name) {
        this.name = name;
        this.isUnsigned = name.startsWith("U");
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int sqlTypeId() {
        return Types.TINYINT;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public Class javaTypeClass() {
        return Byte.class;
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public int getPrecision() {
        return isUnsigned ? 3 : 4;
    }

    @Override
    public int getScale() {
        return 0;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        serializer.writeByte(((Number) data).byteValue());
    }

    @Override
    public Number deserializeBinary(BinaryDeserializer deserializer) throws IOException {
        byte b = deserializer.readByte();
        if (isUnsigned) {
            return (short) (b & 0xff);
        }
        return b;
    }

    @Override
    public Number[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws IOException {
        Number[] data = new Number[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializeBinary(deserializer);
        }
        return data;
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        return lexer.numberLiteral().byteValue();
    }
}
