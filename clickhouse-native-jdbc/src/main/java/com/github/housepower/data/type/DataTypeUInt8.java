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

import com.github.housepower.misc.SQLLexer;
import io.netty.buffer.ByteBuf;

import java.sql.SQLException;

public class DataTypeUInt8 implements BaseDataTypeInt8<Short, Short> {

    @Override
    public String name() {
        return "UInt8";
    }

    @Override
    public Short defaultValue() {
        return 0;
    }

    @Override
    public Class<Short> javaType() {
        return Short.class;
    }

    @Override
    public int getPrecision() {
        return 3;
    }

    @Override
    public void encode(ByteBuf buf, Short data) {
        buf.writeByte(data.byteValue());
    }

    @Override
    public Short decode(ByteBuf buf) {
        return buf.readUnsignedByte();
    }

    @Override
    public Short deserializeText(SQLLexer lexer) throws SQLException {
        return lexer.numberLiteral().shortValue();
    }
}
