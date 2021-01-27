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
import com.github.housepower.misc.BytesHelper;
import com.github.housepower.misc.SQLLexer;
import com.github.housepower.misc.Validate;
import com.github.housepower.serde.BinaryDeserializer;
import com.github.housepower.serde.BinarySerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Locale;

public class DataTypeDecimal implements IDataType<BigDecimal, BigDecimal>, BytesHelper {

    public static DataTypeCreator<BigDecimal, BigDecimal> creator = (lexer, serverContext) -> {
        Validate.isTrue(lexer.character() == '(');
        Number precision = lexer.numberLiteral();
        Validate.isTrue(lexer.character() == ',');
        Number scale = lexer.numberLiteral();
        Validate.isTrue(lexer.character() == ')');
        return new DataTypeDecimal("Decimal(" + precision.intValue() + "," + scale.intValue() + ")",
                precision.intValue(), scale.intValue());
    };

    private final String name;
    private final int precision;
    private final int scale;
    private final BigDecimal scaleFactor;
    private final int nobits;

    // see https://clickhouse.tech/docs/en/sql-reference/data-types/decimal/
    public DataTypeDecimal(String name, int precision, int scale) {
        this.name = name;
        this.precision = precision;
        this.scale = scale;
        this.scaleFactor = BigDecimal.valueOf(Math.pow(10, scale));
        if (this.precision <= 9) {
            this.nobits = 32;
        } else if (this.precision <= 18) {
            this.nobits = 64;
        } else if (this.precision <= 38) {
            this.nobits = 128;
        } else if (this.precision <= 76) {
            this.nobits = 256;
        } else {
            throw new IllegalArgumentException(String.format(Locale.ENGLISH,
                    "Precision[%d] is out of boundary.", precision));
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public int sqlTypeId() {
        return Types.DECIMAL;
    }

    @Override
    public BigDecimal defaultValue() {
        return BigDecimal.ZERO;
    }

    @Override
    public Class<BigDecimal> javaType() {
        return BigDecimal.class;
    }

    @Override
    public int getPrecision() {
        return precision;
    }

    @Override
    public int getScale() {
        return scale;
    }

    @Override
    public BigDecimal deserializeText(SQLLexer lexer) throws SQLException {
        BigDecimal result;
        if (lexer.isCharacter('\'')) {
            String v = lexer.stringLiteral();
            result = new BigDecimal(v);
        } else {
            Number v = lexer.numberLiteral();
            result = BigDecimal.valueOf(v.doubleValue());
        }
        result = result.setScale(scale, RoundingMode.HALF_UP);
        return result;
    }

    @Override
    public void serializeBinary(BigDecimal data, BinarySerializer serializer) throws IOException {
        BigDecimal targetValue = data.multiply(scaleFactor);
        switch (this.nobits) {
            case 32: {
                serializer.writeInt(targetValue.intValue());
                break;
            }
            case 64: {
                serializer.writeLong(targetValue.longValue());
                break;
            }
            case 128: {
                BigInteger res = targetValue.toBigInteger();
                serializer.writeLong(res.longValue());
                serializer.writeLong(res.shiftRight(64).longValue());
                break;
            }
            case 256: {
                BigInteger res = targetValue.toBigInteger();
                serializer.writeLong(targetValue.longValue());
                serializer.writeLong(res.shiftRight(64).longValue());
                serializer.writeLong(res.shiftRight(64 * 2).longValue());
                serializer.writeLong(res.shiftRight(64 * 3).longValue());
                break;
            }
            default: {
                throw new RuntimeException(String.format(Locale.ENGLISH,
                        "Unknown precision[%d] & scale[%d]", precision, scale));
            }
        }
    }

    @Override
    public BigDecimal deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        BigDecimal value;
        switch (this.nobits) {
            case 32: {
                int v = deserializer.readInt();
                value = BigDecimal.valueOf(v);
                value = value.divide(scaleFactor, scale, RoundingMode.HALF_UP);
                break;
            }
            case 64: {
                long v = deserializer.readLong();
                value = BigDecimal.valueOf(v);
                value = value.divide(scaleFactor, scale, RoundingMode.HALF_UP);
                break;
            }

            case 128: {
                long []array = new long[2];
                array[1] = deserializer.readLong();
                array[0] = deserializer.readLong();

                value = new BigDecimal(new BigInteger(getBytes(array)));
                value = value.divide(scaleFactor, scale, RoundingMode.HALF_UP);
                break;
            }

            case 256: {
                long []array = new long[4];
                array[3] = deserializer.readLong();
                array[2] = deserializer.readLong();
                array[1] = deserializer.readLong();
                array[0] = deserializer.readLong();

                value = new BigDecimal(new BigInteger(getBytes(array)));
                value = value.divide(scaleFactor, scale, RoundingMode.HALF_UP);
                break;
            }

            default: {
                throw new RuntimeException(String.format(Locale.ENGLISH,
                        "Unknown precision[%d] & scale[%d]", precision, scale));
            }
        }
        return value;
    }

    @Override
    public boolean isSigned() {
        return true;
    }
}
