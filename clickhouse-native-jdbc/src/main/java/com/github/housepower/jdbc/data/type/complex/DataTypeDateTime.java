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

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.*;

import com.github.housepower.jdbc.connect.NativeContext;
import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.DateTimeUtil;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

public class DataTypeDateTime implements IDataType {

    public static DataTypeCreator creator = (lexer, serverContext) -> {
        if (lexer.isCharacter('(')) {
            Validate.isTrue(lexer.character() == '(');
            String dataTimeZone = lexer.stringLiteral();
            Validate.isTrue(lexer.character() == ')');
            return new DataTypeDateTime("DateTime('" + dataTimeZone + "')", serverContext);
        }
        return new DataTypeDateTime("DateTime", serverContext);
    };

    private static final LocalDateTime EPOCH_LOCAL_DT = LocalDateTime.of(1970, 1, 1, 0, 0);
    private final String name;
    private final ZoneId tz;
    private final ZonedDateTime defaultValue;

    public DataTypeDateTime(String name, NativeContext.ServerContext serverContext) {
        this.name = name;
        this.tz = DateTimeUtil.chooseTimeZone(serverContext);
        this.defaultValue = EPOCH_LOCAL_DT.atZone(tz);
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
        return defaultValue;
    }

    @Override
    public Class javaType() {
        return ZonedDateTime.class;
    }

    @Override
    public Class jdbcJavaType() {
        return Timestamp.class;
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public int getPrecision() {
        return 0;
    }

    @Override
    public int getScale() {
        return 10;
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
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
        int seconds = lexer.numberLiteral().intValue();
        Validate.isTrue(lexer.character() == '\'');

        return ZonedDateTime.of(year, month, day, hours, minutes, seconds, 0, tz);
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        ZonedDateTime zdt = (ZonedDateTime) data;
        serializer.writeInt((int) DateTimeUtil.toEpochSecond(zdt));
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        int epochSeconds = deserializer.readInt();
        return DateTimeUtil.toZonedDateTime(epochSeconds, 0, tz);
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        ZonedDateTime[] data = new ZonedDateTime[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = (ZonedDateTime) deserializeBinary(deserializer);
        }
        return data;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"TIMESTAMP"};
    }
}
