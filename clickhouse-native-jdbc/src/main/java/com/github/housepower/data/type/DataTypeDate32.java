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
import com.github.housepower.misc.Validate;
import com.github.housepower.serde.BinaryDeserializer;
import com.github.housepower.serde.BinarySerializer;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

public class DataTypeDate32 implements IDataType<LocalDate, Date> {

    // First day of Date32
    private static final LocalDate DEFAULT_VALUE = LocalDate.of(1925, 1, 1);

    public DataTypeDate32() {
    }

    @Override
    public String name() {
        return "Date32";
    }

    @Override
    public int sqlTypeId() {
        return Types.DATE;
    }

    @Override
    public LocalDate defaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public Class<LocalDate> javaType() {
        return LocalDate.class;
    }

    @Override
    public Class<Date> jdbcJavaType() {
        return Date.class;
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
    public void serializeBinary(LocalDate data, BinarySerializer serializer) throws SQLException, IOException {
        long epochDay = data.toEpochDay();
        serializer.writeInt((int) epochDay);
    }

    @Override
    public LocalDate deserializeBinary(BinaryDeserializer deserializer) throws IOException {
        int epochDay = deserializer.readInt();
        return LocalDate.ofEpochDay(epochDay);
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public LocalDate deserializeText(SQLLexer lexer) throws SQLException {
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
