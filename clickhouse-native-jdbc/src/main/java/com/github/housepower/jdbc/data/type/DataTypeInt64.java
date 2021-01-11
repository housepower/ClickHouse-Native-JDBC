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

import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;

public class DataTypeInt64 implements BaseDataTypeInt64<Long, Long> {

    private final String name;

    public DataTypeInt64(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Long defaultValue() {
        return 0L;
    }

    @Override
    public Class<Long> javaType() {
        return Long.class;
    }

    @Override
    public int getPrecision() {
        return 20;
    }

    @Override
    public void serializeBinary(Long data, BinarySerializer serializer) throws SQLException, IOException {
        serializer.writeLong(((Number) data).longValue());
    }

    @Override
    public Long deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        return deserializer.readLong();
    }

    @Override
    public String[] getAliases() {
        return new String[]{"BIGINT"};
    }

    @Override
    public Long deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        return lexer.numberLiteral().longValue();
    }

    @Override
    public boolean isSigned() {
        return true;
    }
}
