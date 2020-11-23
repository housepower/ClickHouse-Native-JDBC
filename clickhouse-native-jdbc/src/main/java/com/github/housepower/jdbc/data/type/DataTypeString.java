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

public class DataTypeString implements IDataType {
    private Charset charset;

    public DataTypeString(NativeContext.ServerContext serverContext) {
        this.charset = serverContext.getConfigure().charset();
    }

    @Override
    public String name() {
        return "String";
    }

    @Override
    public int sqlTypeId() {
        return Types.VARCHAR;
    }

    @Override
    public Object defaultValue() {
        return "";
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
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        if (data instanceof String) {
            byte []bs = ((String) data).getBytes(charset);
            serializer.writeBytesBinary(bs);
        } else if (data instanceof StringView) {
            serializer.writeStringViewBinary((StringView) data, charset);
        } else {
            serializer.writeBytesBinary((byte []) data);
        }
    }


    /**
     * deserializeBinary will always returns String
     * for getBytes(idx) method, we encode the String again
     * @param deserializer
     * @return
     * @throws SQLException
     * @throws IOException
     */
    @Override
    public String deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        byte []bs = deserializer.readBytesBinary();
        return new String(bs, charset);
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        String[] data = new String[rows];
        for (int row = 0; row < rows; row++) {
            byte []bs = deserializer.readBytesBinary();
            data[row] = new String(bs, charset);
        }
        return data;
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        return lexer.stringView();
    }
}
