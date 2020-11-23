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
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.time.*;

public class DataTypeDate implements IDataType {

    public static IDataType createDateType(SQLLexer lexer, NativeContext.ServerContext serverContext) {
        return new DataTypeDate();
    }

    // Since `Date` is mutable, and `defaultValue()` will return ref instead of a copy for performance,
    // we should ensure DON'T modify it anywhere.
    private static final Date DEFAULT_VALUE = Date.valueOf(LocalDate.ofEpochDay(0));

    public DataTypeDate() {
    }

    @Override
    public String name() {
        return "Date";
    }

    @Override
    public int sqlTypeId() {
        return Types.DATE;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public Class javaTypeClass() {
        return Date.class;
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public int getPrecision() {
        return 10;
    }

    @Override
    public int getScale() {
        return 0;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        long epochDay = ((Date) data).toLocalDate().toEpochDay();
        serializer.writeShort((short) epochDay);
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws IOException {
        short daysSinceEpoch = deserializer.readShort();
        return new Date(3600L * 24 * 1000 * daysSinceEpoch);
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws IOException {
        Date[] data = new Date[rows];
        for (int row = 0; row < rows; row++) {
            short daysSinceEpoch = deserializer.readShort();
            data[row] = Date.valueOf(LocalDate.ofEpochDay(daysSinceEpoch));
        }
        return data;
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        Validate.isTrue(lexer.character() == '\'');
        int year = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == '-');
        int month = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == '-');
        int day = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == '\'');

        LocalDate date = LocalDate.of(year, month, day);
        return Date.valueOf(date);
    }
}
