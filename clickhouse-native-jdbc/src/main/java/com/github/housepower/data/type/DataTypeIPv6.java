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
import com.github.housepower.misc.SQLLexer;
import com.github.housepower.serde.BinaryDeserializer;
import com.github.housepower.serde.BinarySerializer;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Types;

public class DataTypeIPv6 implements IDataType<BigInteger, BigInteger> {

    @Override
    public String name() {
        return "IPv6";
    }

    @Override
    public int sqlTypeId() {
        return Types.BIGINT;
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
        return 0;
    }

    @Override
    public int getScale() {
        return 39;
    }

    @Override
    public void serializeBinary(BigInteger data, BinarySerializer serializer) throws SQLException, IOException {
        byte[] bytes = data.toByteArray();
        if (bytes.length > 16) {
            throw new SQLException("IPv6 representation exceeds 16 bytes.");
        }
        byte[] paddedBytes = new byte[16];
        int offset = 16 - bytes.length;
        System.arraycopy(bytes, 0, paddedBytes, offset, bytes.length);
        serializer.writeBytes(paddedBytes, 0, paddedBytes.length);
    }

    @Override
    public BigInteger deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        byte[] bytes = deserializer.readBytes(16);
        return new BigInteger(1, bytes);  // force it to be positive
    }
    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public BigInteger deserializeText(SQLLexer lexer) throws SQLException {
        String ipv6String = convertIPv6ToHexadecimalString(lexer.stringLiteral());
        return new BigInteger(ipv6String, 16);
    }

    private static String convertIPv6ToHexadecimalString(String ipv6) {
        return ipv6.replace(":", "");
    }
}
