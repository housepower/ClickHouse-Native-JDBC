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
import com.github.housepower.data.type.DataTypeInt64;
import com.github.housepower.jdbc.ClickHouseArray;
import com.github.housepower.misc.ExceptionUtil;
import com.github.housepower.misc.SQLLexer;
import com.github.housepower.misc.Validate;
import io.netty.buffer.ByteBuf;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO avoid using ClickHouseArray because it's a subclass of java.sql.Array
public class DataTypeArray implements IDataType<ClickHouseArray, Array> {

    public static DataTypeCreator<ClickHouseArray, Array> creator = (lexer, serverContext) -> {
        Validate.isTrue(lexer.character() == '(');
        IDataType<?, ?> arrayNestedType = DataTypeFactory.get(lexer, serverContext);
        Validate.isTrue(lexer.character() == ')');
        return new DataTypeArray("Array(" + arrayNestedType.name() + ")",
                arrayNestedType, (DataTypeInt64) DataTypeFactory.get("Int64", serverContext));
    };

    private final String name;
    private final ClickHouseArray defaultValue;


    private final IDataType<?, ?> elemDataType;
    // Change from UInt64 to Int64 because we mapping UInt64 to BigInteger
    private final DataTypeInt64 offsetIDataType;

    public DataTypeArray(String name, IDataType<?, ?> elemDataType, DataTypeInt64 offsetIDataType) throws SQLException {
        this.name = name;
        this.elemDataType = elemDataType;
        this.offsetIDataType = offsetIDataType;
        this.defaultValue = new ClickHouseArray(elemDataType, new Object[]{elemDataType.defaultValue()});
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int sqlTypeId() {
        return Types.ARRAY;
    }

    @Override
    public ClickHouseArray defaultValue() {
        return defaultValue;
    }

    @Override
    public Class<ClickHouseArray> javaType() {
        return ClickHouseArray.class;
    }

    @Override
    public Class<Array> jdbcJavaType() {
        return Array.class;
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
    public ClickHouseArray deserializeText(SQLLexer lexer) throws SQLException {
        Validate.isTrue(lexer.character() == '[');
        List<Object> arrayData = new ArrayList<>();
        for (; ; ) {
            if (lexer.isCharacter(']')) {
                lexer.character();
                break;
            }
            if (lexer.isCharacter(',')) {
                lexer.character();
            }
            arrayData.add(elemDataType.deserializeText(lexer));
        }
        return new ClickHouseArray(elemDataType, arrayData.toArray());
    }

    @Override
    public void encode(ByteBuf buf, ClickHouseArray data) {
        try {
            for (Object f : data.getArray()) {
                getElemDataType().encode(buf, f);
            }
        } catch (Exception ex) {
            throw ExceptionUtil.unchecked(ex);
        }

    }

    @Override
    public void encodeBulk(ByteBuf buf, ClickHouseArray[] data) {
        try {
            offsetIDataType.encode(buf, (long) data.length);
            getElemDataType().encodeBulk(buf, data);
        } catch (Exception ex) {
            throw ExceptionUtil.unchecked(ex);
        }
    }

    @Override
    public ClickHouseArray decode(ByteBuf buf) {
        Long offset = offsetIDataType.decode(buf);
        Object[] data = getElemDataType().decodeBulk(buf, offset.intValue());
        return new ClickHouseArray(elemDataType, data);
    }

    @Override
    public Object[] decodeBulk(ByteBuf buf, int rows) {
        ClickHouseArray[] arrays = new ClickHouseArray[rows];
        if (rows == 0) {
            return arrays;
        }

        int[] offsets = Arrays.stream(offsetIDataType.decodeBulk(buf, rows)).mapToInt(value -> ((Long) value).intValue()).toArray();
        ClickHouseArray res = new ClickHouseArray(elemDataType,
                elemDataType.decodeBulk(buf, offsets[rows - 1]));

        for (int row = 0, lastOffset = 0; row < rows; row++) {
            int offset = offsets[row];
            arrays[row] = res.slice(lastOffset, offset - lastOffset);
            lastOffset = offset;
        }
        return arrays;
    }

    public IDataType getElemDataType() {
        return elemDataType;
    }
}
