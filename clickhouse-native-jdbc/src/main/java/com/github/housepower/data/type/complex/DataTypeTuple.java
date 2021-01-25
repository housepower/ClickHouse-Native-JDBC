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

import com.github.housepower.jdbc.ClickHouseStruct;
import com.github.housepower.data.DataTypeFactory;
import com.github.housepower.data.IDataType;
import com.github.housepower.misc.SQLLexer;
import com.github.housepower.misc.Validate;
import com.github.housepower.serde.BinaryDeserializer;
import com.github.housepower.serde.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DataTypeTuple implements IDataType<ClickHouseStruct, Struct> {

    public static DataTypeCreator<ClickHouseStruct, Struct> creator = (lexer, serverContext) -> {
        Validate.isTrue(lexer.character() == '(');
        List<IDataType<?, ?>> nestedDataTypes = new ArrayList<>();

        for (; ; ) {
            nestedDataTypes.add(DataTypeFactory.get(lexer, serverContext));
            char delimiter = lexer.character();
            Validate.isTrue(delimiter == ',' || delimiter == ')');
            if (delimiter == ')') {
                StringBuilder builder = new StringBuilder("Tuple(");
                for (int i = 0; i < nestedDataTypes.size(); i++) {
                    if (i > 0)
                        builder.append(",");
                    builder.append(nestedDataTypes.get(i).name());
                }
                return new DataTypeTuple(builder.append(")").toString(), nestedDataTypes.toArray(new IDataType[0]));
            }
        }
    };

    private final String name;
    private final IDataType<?, ?>[] nestedTypes;

    public DataTypeTuple(String name, IDataType<?, ?>[] nestedTypes) {
        this.name = name;
        this.nestedTypes = nestedTypes;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int sqlTypeId() {
        return Types.STRUCT;
    }

    @Override
    public ClickHouseStruct defaultValue() {
        Object[] attrs = new Object[getNestedTypes().length];
        for (int i = 0; i < getNestedTypes().length; i++) {
            attrs[i] = getNestedTypes()[i].defaultValue();
        }
        return new ClickHouseStruct("Tuple", attrs);
    }

    @Override
    public Class<ClickHouseStruct> javaType() {
        return ClickHouseStruct.class;
    }

    @Override
    public Class<Struct> jdbcJavaType() {
        return Struct.class;
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
    public void serializeBinary(ClickHouseStruct data, BinarySerializer serializer) throws SQLException, IOException {
        for (int i = 0; i < getNestedTypes().length; i++) {
            getNestedTypes()[i].serializeBinary(data.getAttributes()[i], serializer);
        }
    }

    @Override
    public void serializeBinaryBulk(ClickHouseStruct[] data, BinarySerializer serializer) throws SQLException, IOException {
        for (int i = 0; i < getNestedTypes().length; i++) {
            Object[] elemsData = new Object[data.length];
            for (int row = 0; row < data.length; row++) {
                elemsData[row] = ((Struct) data[row]).getAttributes()[i];
            }
            getNestedTypes()[i].serializeBinaryBulk(elemsData, serializer);
        }
    }

    @Override
    public ClickHouseStruct deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        Object[] attrs = new Object[getNestedTypes().length];
        for (int i = 0; i < getNestedTypes().length; i++) {
            attrs[i] = getNestedTypes()[i].deserializeBinary(deserializer);
        }
        return new ClickHouseStruct("Tuple", attrs);
    }

    @Override
    public ClickHouseStruct[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        Object[][] rowsWithElems = getRowsWithElems(rows, deserializer);

        ClickHouseStruct[] rowsData = new ClickHouseStruct[rows];
        for (int row = 0; row < rows; row++) {
            Object[] elemsData = new Object[getNestedTypes().length];

            for (int elemIndex = 0; elemIndex < getNestedTypes().length; elemIndex++) {
                elemsData[elemIndex] = rowsWithElems[elemIndex][row];
            }
            rowsData[row] = new ClickHouseStruct("Tuple", elemsData);
        }
        return rowsData;
    }

    private Object[][] getRowsWithElems(int rows, BinaryDeserializer deserializer) throws IOException, SQLException {
        Object[][] rowsWithElems = new Object[getNestedTypes().length][];
        for (int index = 0; index < getNestedTypes().length; index++) {
            rowsWithElems[index] = getNestedTypes()[index].deserializeBinaryBulk(rows, deserializer);
        }
        return rowsWithElems;
    }

    @Override
    public ClickHouseStruct deserializeText(SQLLexer lexer) throws SQLException {
        Validate.isTrue(lexer.character() == '(');
        Object[] tupleData = new Object[getNestedTypes().length];
        for (int i = 0; i < getNestedTypes().length; i++) {
            if (i > 0)
                Validate.isTrue(lexer.character() == ',');
            tupleData[i] = getNestedTypes()[i].deserializeText(lexer);
        }
        Validate.isTrue(lexer.character() == ')');
        return new ClickHouseStruct("Tuple", tupleData);
    }

    public IDataType[] getNestedTypes() {
        return nestedTypes;
    }
}
