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

import com.github.housepower.misc.CodecHelper;
import com.github.housepower.misc.SQLLexer;
import io.netty.buffer.ByteBuf;

import java.math.BigInteger;
import java.sql.SQLException;

// I see some binary protocol frameworks such as Protobuf chose an alternative way to represent UInt64 by long,
// and use special tools to calculate it. Since currently we don't guarantee any stable APIs except JDBC APIs,
// so we have an opportunity to change it later.
public class DataTypeUInt64 implements BaseDataTypeInt64<BigInteger, BigInteger>, CodecHelper {

    @Override
    public String name() {
        return "UInt64";
    }

    @Override
    public BigInteger defaultValue() {
        return BigInteger.ZERO;
    }

    @Override
    public Class<BigInteger> javaType() {
        return BigInteger.class;
    }

    @Override
    public int getPrecision() {
        return 19;
    }

    @Override
    public void encode(ByteBuf buf, BigInteger data) {
        buf.writeLongLE(data.longValue());
    }

    @Override
    public BigInteger decode(ByteBuf buf) {
        long l = buf.readLongLE();
        return new BigInteger(1, getBytes(l));
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public BigInteger deserializeText(SQLLexer lexer) throws SQLException {
        return BigInteger.valueOf(lexer.numberLiteral().longValue());
    }
}
