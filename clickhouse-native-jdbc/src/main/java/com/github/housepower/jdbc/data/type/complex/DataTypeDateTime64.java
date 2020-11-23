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

import com.github.housepower.jdbc.connect.NativeContext.ServerContext;
import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.DateTimeHelper;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.StringView;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DataTypeDateTime64 implements IDataType {

    public static DataTypeDateTime64 createDateTime64Type(SQLLexer lexer, ServerContext serverContext) throws SQLException {
        if (lexer.isCharacter('(')) {
            Validate.isTrue(lexer.character() == '(');
            int scale = lexer.numberLiteral().intValue();
            Validate.isTrue(scale >= MIN_SCALE && scale <= MAX_SCALA,
                    "scale=" + scale + " out of range [" + MIN_SCALE + "," + MAX_SCALA + "]");
            if (lexer.isCharacter(',')) {
                Validate.isTrue(lexer.character() == ',');
                Validate.isTrue(lexer.isWhitespace());
                String dataTimeZone = lexer.stringLiteral();
                Validate.isTrue(lexer.character() == ')');
                return new DataTypeDateTime64("DateTime64(" + scale + ", '" + dataTimeZone + "')", scale, serverContext);
            }

            Validate.isTrue(lexer.character() == ')');
            return new DataTypeDateTime64("DateTime64(" + scale + ")", scale, serverContext);
        }
        return new DataTypeDateTime64("DateTime64", DEFAULT_SCALE, serverContext);
    }

    // Since `Timestamp` is mutable, and `defaultValue()` will return ref instead of a copy for performance,
    // we should ensure DON'T modify it anywhere.
    public static final Timestamp DEFAULT_VALUE = new Timestamp(0);
    public static final int NANOS_IN_SECOND = 1_000_000_000;
    public static final int MILLIS_IN_SECOND = 1000;
    public static final int[] POW_10 = {1, 10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000, 1_000_000_000};
    public static final int MIN_SCALE = 0;
    public static final int MAX_SCALA = 9;
    public static final int DEFAULT_SCALE = 3;

    private final String name;
    private final int scale;
    private final ZoneId tz;

    public DataTypeDateTime64(String name, int scala, ServerContext serverContext) {
        this.name = name;
        this.scale = scala;
        this.tz = DateTimeHelper.chooseTimeZone(serverContext);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int sqlTypeId() {
        return Types.TIMESTAMP;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public Class javaTypeClass() {
        return Timestamp.class;
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public int getPrecision() {
        return 20;
    }

    @Override
    public int getScale() {
        return scale;
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        StringView dataTypeName = lexer.bareWord();
        Validate.isTrue(dataTypeName.checkEquals("toDateTime64"));
        Validate.isTrue(lexer.character() == '(');
        Validate.isTrue(lexer.character() == '\'');
        int year = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == '-');
        int month = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == '-');
        int day = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.isWhitespace());
        int hours = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == ':');
        int minutes = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == ':');
        BigDecimal _seconds = BigDecimal.valueOf(lexer.numberLiteral().doubleValue())
                .setScale(scale, BigDecimal.ROUND_HALF_UP);
        int second = _seconds.intValue();
        int nanos = _seconds.subtract(BigDecimal.valueOf(second)).movePointRight(9).intValue();
        Validate.isTrue(lexer.character() == '\'');
        Validate.isTrue(lexer.character() == ')');

        ZonedDateTime zdt = ZonedDateTime.of(year, month, day, hours, minutes, second, nanos, tz);
        return Timestamp.from(zdt.toInstant());
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws IOException {
        long value = deserializer.readLong() * POW_10[MAX_SCALA - scale];
        long epochSeconds = value / NANOS_IN_SECOND;
        int nanos = (int) (value % NANOS_IN_SECOND);
        Timestamp timestamp = new Timestamp(epochSeconds * 1000);
        if (nanos != 0)
            timestamp.setNanos(nanos);
        return timestamp;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws IOException {
        Timestamp timestamp = (Timestamp) data;
        long epochSeconds = timestamp.getTime() / MILLIS_IN_SECOND;
        int nanos = timestamp.getNanos();
        long value = (epochSeconds * NANOS_IN_SECOND + nanos) / POW_10[MAX_SCALA - scale];
        serializer.writeLong(value);
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws IOException {
        Timestamp[] data = new Timestamp[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = (Timestamp) deserializeBinary(deserializer);
        }
        return data;
    }
}
