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

import com.github.housepower.jdbc.misc.BytesUtil;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;

public class DataTypeUInt64 implements BaseDataTypeInt64<BigInteger, BigInteger> {

    private final String name;

    public DataTypeUInt64(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
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
    public void serializeBinary(BigInteger data, BinarySerializer serializer) throws SQLException, IOException {
        serializer.writeLong(((Number) data).longValue());
    }

    @Override
    public BigInteger deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        long l = deserializer.readLong();
        return new BigInteger(1, BytesUtil.longToBytes(l));
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public BigInteger deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        return BigInteger.valueOf(lexer.numberLiteral().longValue());
    }
}