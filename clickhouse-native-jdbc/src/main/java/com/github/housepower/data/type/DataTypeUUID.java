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
import com.github.housepower.io.ISink;
import com.github.housepower.io.ISource;
import com.github.housepower.misc.SQLLexer;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

public class DataTypeUUID implements IDataType<UUID, String> {

    @Override
    public String name() {
        return "UUID";
    }

    @Override
    public int sqlTypeId() {
        return Types.VARCHAR;
    }

    @Override
    public UUID defaultValue() {
        return null;
    }

    @Override
    public Class<UUID> javaType() {
        return UUID.class;
    }

    @Override
    public Class<String> jdbcJavaType() {
        return String.class;
    }

    @Override
    public int getPrecision() {
        return 36;
    }

    @Override
    public int getScale() {
        return 0;
    }

    @Override
    public UUID deserializeText(SQLLexer lexer) throws SQLException {
        return UUID.fromString(lexer.stringLiteral());
    }

    @Override
    public void serializeBinary(UUID data, ISink sink) throws SQLException, IOException {
        sink.writeLongLE(data.getMostSignificantBits());
        sink.writeLongLE(data.getLeastSignificantBits());
    }

    @Override
    public UUID deserializeBinary(ISource source) throws SQLException, IOException {
        return new UUID(source.readLongLE(), source.readLongLE());
    }
}
