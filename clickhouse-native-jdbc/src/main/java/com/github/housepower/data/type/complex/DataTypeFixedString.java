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

import com.github.housepower.client.NativeContext;
import com.github.housepower.data.IDataType;
import com.github.housepower.exception.ClickHouseClientException;
import com.github.housepower.io.ISink;
import com.github.housepower.io.ISource;
import com.github.housepower.misc.SQLLexer;
import com.github.housepower.misc.Validate;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.AsciiString;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Types;

public class DataTypeFixedString implements IDataType<CharSequence, String> {

    public static DataTypeCreator<CharSequence, String> creator = (lexer, serverContext) -> {
        Validate.isTrue(lexer.character() == '(');
        Number fixedStringN = lexer.numberLiteral();
        Validate.isTrue(lexer.character() == ')');
        return new DataTypeFixedString("FixedString(" + fixedStringN.intValue() + ")", fixedStringN.intValue(), serverContext);
    };

    private final int n;
    private final String name;
    private final String defaultValue;
    private final Charset charset;

    public DataTypeFixedString(String name, int n, NativeContext.ServerContext serverContext) {
        this.n = n;
        this.name = name;
        this.charset = serverContext.getConfigure().charset();
        this.defaultValue = new String(new byte[n], charset);
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
    public String defaultValue() {
        return defaultValue;
    }

    @Override
    public Class<CharSequence> javaType() {
        return CharSequence.class;
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
    public void serializeBinary(CharSequence data, ISink sink) throws SQLException, IOException {
        int writeLen;
        int paddingLen;
        if (data instanceof AsciiString) {
            writeLen = data.length();
            checkWriteLength(writeLen);
            sink.writeCharSequence(data, StandardCharsets.US_ASCII);
        } else if (charset.equals(StandardCharsets.UTF_8)) {
            writeLen = ByteBufUtil.utf8Bytes(data);
            checkWriteLength(writeLen);
            sink.writeCharSequence(data, charset);
        } else {
            byte[] bytes = data.toString().getBytes(charset);
            writeLen = bytes.length;
            checkWriteLength(writeLen);
            sink.writeBytes(Unpooled.wrappedBuffer(bytes));
        }
        paddingLen = n - writeLen;
        sink.writeZero(paddingLen);
    }

    private void checkWriteLength(int writeLen) {
        if (writeLen > n)
            throw new ClickHouseClientException("The size of FixString column is too large, got " + writeLen);
    }

    @Override
    public CharSequence deserializeBinary(ISource source) throws SQLException, IOException {
        return source.readCharSequence(n, charset);
    }

    @Override
    public CharSequence deserializeText(SQLLexer lexer) throws SQLException {
        return lexer.stringLiteral();
    }

    @Override
    public String[] getAliases() {
        return new String[]{"BINARY"};
    }
}
