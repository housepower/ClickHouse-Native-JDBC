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

public class DataTypeUInt16 implements BaseDataTypeInt16<Integer, Integer> {

    @Override
    public String name() {
        return "UInt16";
    }

    @Override
    public Integer defaultValue() {
        return 0;
    }

    @Override
    public Class<Integer> javaType() {
        return Integer.class;
    }

    @Override
    public int getPrecision() {
        return 5;
    }

    @Override
    public void encode(ByteBuf buf, Integer data) {
        buf.writeShortLE(data.shortValue());
    }

    @Override
    public Integer decode(ByteBuf buf) {
        return buf.readUnsignedShortLE();
    }

    @Override
    public Integer deserializeText(SQLLexer lexer) throws SQLException {
        return lexer.numberLiteral().intValue();
    }
}
