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

import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.Types;

public class DataTypeInt64 implements IDataType {

    private static final Long DEFAULT_VALUE = 0L;
    private final String name;
    private final boolean isUnsigned;

    public DataTypeInt64(String name) {
        this.name = name;
        this.isUnsigned = name.startsWith("U");
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int sqlTypeId() {
        return Types.BIGINT;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public Class javaTypeClass() {
        return Long.class;
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public int getPrecision() {
        return isUnsigned ? 19 : 20;
    }

    @Override
    public int getScale() {
        return 0;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer)
            throws SQLException, IOException {
        serializer.writeLong(((Number) data).longValue());
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer)
            throws SQLException, IOException {
        long l = deserializer.readLong();
        if (isUnsigned) {
            return new BigInteger(1, longToBytes(l));
        }
        return l;
    }

    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public BigInteger parseBigIntegerPositive(String num, int bitlen) {
        BigInteger b = new BigInteger(num);
        if (b.compareTo(BigInteger.ZERO) < 0) {
            b = b.add(BigInteger.ONE.shiftLeft(bitlen));
        }
        return b;
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer)
            throws SQLException, IOException {
        Object[] data = new Object[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = this.deserializeBinary(deserializer);
        }
        return data;
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        return lexer.numberLiteral().longValue();
    }
}
