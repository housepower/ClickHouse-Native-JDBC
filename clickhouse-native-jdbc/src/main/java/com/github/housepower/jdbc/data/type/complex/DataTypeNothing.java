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
import com.github.housepower.jdbc.misc.StringView;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Types;

public class DataTypeNothing implements IDataType {

    public static DataTypeCreator CREATOR = (lexer, serverContext) -> new DataTypeNothing(serverContext);

    private final Charset charset;

    public DataTypeNothing(NativeContext.ServerContext serverContext) {
        this.charset = serverContext.getConfigure().charset();
    }

    @Override
    public String name() {
        return "Nothing";
    }

    @Override
    public int sqlTypeId() {
        return Types.NULL;
    }

    @Override
    public Object defaultValue() {
        return "";
    }

    @Override
    public Class javaTypeClass() {
        return Object.class;
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
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        if (data instanceof String) {
            byte[] bs = ((String) data).getBytes(charset);
            serializer.writeBytesBinary(bs);
        } else if (data instanceof StringView) {
            serializer.writeStringViewBinary((StringView) data, charset);
        } else {
            serializer.writeBytesBinary((byte[]) data);
        }
    }

    /**
     * deserializeBinary will always returns String
     * for getBytes(idx) method, we encode the String again
     */
    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        return new Object();
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        Object[] data = new Object[rows];
        return data;
    }

    @Override
    public String[] getAliases() {
        return new String[]{
                "NULL"};
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        return lexer.stringView();
    }
}
