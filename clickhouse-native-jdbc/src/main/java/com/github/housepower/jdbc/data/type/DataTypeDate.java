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
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

public class DataTypeDate implements IDataType {
    // Since `Date` is mutable, and `defaultValue()` will return ref instead of a copy for performance,
    // we should ensure DON'T modify it anywhere.
    private static final LocalDate DEFAULT_VALUE = LocalDate.ofEpochDay(0);

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
    public Class javaType() {
        return LocalDate.class;
    }

    @Override
    public Class jdbcJavaType() {
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
        long epochDay = ((LocalDate) data).toEpochDay();
        serializer.writeShort((short) epochDay);
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws IOException {
        short epochDay = deserializer.readShort();
        return LocalDate.ofEpochDay(epochDay);
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws IOException {
        LocalDate[] data = new LocalDate[rows];
        for (int row = 0; row < rows; row++) {
            short epochDay = deserializer.readShort();
            data[row] = LocalDate.ofEpochDay(epochDay);
        }
        return data;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
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

        return LocalDate.of(year, month, day);
    }
}
