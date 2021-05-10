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

import com.github.housepower.data.IDataType;
import com.github.housepower.io.ISink;
import com.github.housepower.io.ISource;
import com.github.housepower.misc.SQLLexer;
import okio.ByteString;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Types;

public class DataTypeString implements IDataType<ByteString, String> {

    public static DataTypeCreator<ByteString, String> CREATOR = (lexer, serverContext) -> new DataTypeString();

    @Override
    public String name() {
        return "String";
    }

    @Override
    public int sqlTypeId() {
        return Types.VARCHAR;
    }

    @Override
    public ByteString defaultValue() {
        return ByteString.EMPTY;
    }

    @Override
    public Class<ByteString> javaType() {
        return ByteString.class;
    }

    @Override
    public Class<String> jdbcJavaType() {
        return String.class;
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
    public void serializeBinary(ByteString data, ISink sink) throws SQLException, IOException {
        sink.writeBinary(data.toByteArray());
    }

    /**
     * deserializeBinary will always returns String
     * for getBytes(idx) method, we encode the String again
     */
    @Override
    public ByteString deserializeBinary(ISource source) throws SQLException, IOException {
        return source.readByteStringBinary();
    }

    @Override
    public ByteString deserializeText(SQLLexer lexer) throws SQLException {
        return ByteString.encodeString(lexer.stringLiteral(), StandardCharsets.UTF_8);
    }

    @Override
    public String[] getAliases() {
        return new String[]{
                "LONGBLOB",
                "MEDIUMBLOB",
                "TINYBLOB",
                "MEDIUMTEXT",
                "CHAR",
                "VARCHAR",
                "TEXT",
                "TINYTEXT",
                "LONGTEXT",
                "BLOB"};
    }
}
