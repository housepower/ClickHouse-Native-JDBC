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

import com.github.housepower.data.DataTypeFactory;
import com.github.housepower.data.IDataType;
import com.github.housepower.misc.SQLLexer;
import com.github.housepower.misc.Validate;
import io.netty.buffer.ByteBuf;

import java.sql.SQLException;

public class DataTypeNullable implements IDataType {

    public static DataTypeCreator creator = (lexer, serverContext) -> {
        Validate.isTrue(lexer.character() == '(');
        IDataType nestedType = DataTypeFactory.get(lexer, serverContext);
        Validate.isTrue(lexer.character() == ')');
        return new DataTypeNullable(
                "Nullable(" + nestedType.name() + ")", nestedType, DataTypeFactory.get("UInt8", serverContext));
    };

    private static final Short IS_NULL = 1;
    private static final Short NON_NULL = 0;

    private final String name;
    private final IDataType nestedDataType;
    private final IDataType nullMapDataType;

    public IDataType getNestedDataType() {
        return nestedDataType;
    }

    public DataTypeNullable(String name, IDataType nestedDataType, IDataType nullMapIDataType) {
        this.name = name;
        this.nestedDataType = nestedDataType;
        this.nullMapDataType = nullMapIDataType;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int sqlTypeId() {
        return nestedDataType.sqlTypeId();
    }

    @Override
    public Object defaultValue() {
        return nestedDataType.defaultValue();
    }

    @Override
    public Class javaType() {
        return nestedDataType.javaType();
    }

    @Override
    public Class jdbcJavaType() {
        return nestedDataType.jdbcJavaType();
    }

    @Override
    public boolean nullable() {
        return true;
    }

    @Override
    public int getPrecision() {
        return 0;
    }

    @Override
    public int getScale() {
        return 0;
    }

    @Override
    public Object deserializeText(SQLLexer lexer) throws SQLException {
        if (lexer.isCharacter('n') || lexer.isCharacter('N')) {
            Validate.isTrue(Character.toLowerCase(lexer.character()) == 'n');
            Validate.isTrue(Character.toLowerCase(lexer.character()) == 'u');
            Validate.isTrue(Character.toLowerCase(lexer.character()) == 'l');
            Validate.isTrue(Character.toLowerCase(lexer.character()) == 'l');
            return null;
        }
        return nestedDataType.deserializeText(lexer);
    }

    @Override
    public void encode(ByteBuf buf, Object data) {
        this.nestedDataType.encode(buf, data);
    }

    @Override
    public void encodeBulk(ByteBuf buf, Object[] data) {
        Short[] isNull = new Short[data.length];
        for (int i = 0; i < data.length; i++) {
            isNull[i] = (data[i] == null ? IS_NULL : NON_NULL);
            data[i] = data[i] == null ? nestedDataType.defaultValue() : data[i];
        }
        nullMapDataType.encodeBulk(buf, isNull);
        nestedDataType.encodeBulk(buf, data);
    }

    @Override
    public Object decode(ByteBuf buf) {
        boolean isNull = (buf.readByte() == (byte) 1);
        if (isNull) {
            return null;
        }
        return this.nestedDataType.decode(buf);
    }

    @Override
    public Object[] decodeBulk(ByteBuf buf, int rows) {
        Object[] nullMap = nullMapDataType.decodeBulk(buf, rows);

        Object[] data = nestedDataType.decodeBulk(buf, rows);
        for (int i = 0; i < nullMap.length; i++) {
            if (IS_NULL.equals(nullMap[i])) {
                data[i] = null;
            }
        }
        return data;
    }

    @Override
    public boolean isSigned() {
        return nestedDataType.isSigned();
    }
}
