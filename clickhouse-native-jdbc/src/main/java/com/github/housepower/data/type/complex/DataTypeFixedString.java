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
import com.github.housepower.exception.ClickHouseClientException;
import com.github.housepower.io.ISink;
import com.github.housepower.io.ISource;
import com.github.housepower.misc.SQLLexer;
import com.github.housepower.misc.Validate;
import okio.ByteString;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Types;

public class DataTypeFixedString implements IDataType<ByteString, String> {

    public static DataTypeCreator<ByteString, String> creator = (lexer, serverContext) -> {
        Validate.isTrue(lexer.character() == '(');
        Number fixedStringN = lexer.numberLiteral();
        Validate.isTrue(lexer.character() == ')');
        return new DataTypeFixedString("FixedString(" + fixedStringN.intValue() + ")", fixedStringN.intValue());
    };

    private final int n;
    private final String name;
    private final ByteString defaultValue;

    public DataTypeFixedString(String name, int n) {
        this.n = n;
        this.name = name;
        this.defaultValue = ByteString.of(new byte[n]);
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
    public ByteString defaultValue() {
        return defaultValue;
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
        return n;
    }

    @Override
    public int getScale() {
        return 0;
    }

    @Override
    public void serializeBinary(ByteString data, ISink sink) throws SQLException, IOException {
        int writeLen = data.size();
        int paddingLen = n - writeLen;
        checkWriteLength(writeLen);
        sink.writeBytes(data.toByteArray());
        sink.writeZero(paddingLen);
    }

    private void checkWriteLength(int writeLen) {
        if (writeLen > n)
            throw new ClickHouseClientException("The size of FixedString column is too large, got " + writeLen);
    }

    @Override
    public ByteString deserializeBinary(ISource source) throws SQLException, IOException {
        return source.readByteString(n);
    }

    @Override
    public ByteString deserializeText(SQLLexer lexer) throws SQLException {
        return ByteString.encodeString(lexer.stringLiteral(), StandardCharsets.UTF_8);
    }

    @Override
    public String[] getAliases() {
        return new String[]{"BINARY"};
    }
}
