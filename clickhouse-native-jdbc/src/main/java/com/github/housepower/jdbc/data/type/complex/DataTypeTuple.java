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

import com.github.housepower.jdbc.ClickHouseStruct;
import com.github.housepower.jdbc.connect.NativeContext;
import com.github.housepower.jdbc.data.DataTypeFactory;
import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DataTypeTuple implements IDataType {

    public static DataTypeTuple createTupleType(SQLLexer lexer,
                                                NativeContext.ServerContext serverContext) throws SQLException {
        Validate.isTrue(lexer.character() == '(');
        List<IDataType> nestedDataTypes = new ArrayList<>();

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
    }

    public IDataType[] getNestedTypes() {
        return nestedTypes;
    }

    private final String name;
    private final IDataType[] nestedTypes;

    public DataTypeTuple(String name, IDataType[] nestedTypes) {
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
    public Object defaultValue() {
        Object[] attrs = new Object[nestedTypes.length];
        for (int i = 0; i < nestedTypes.length; i++) {
            attrs[i] = nestedTypes[i].defaultValue();
        }
        return new ClickHouseStruct("Tuple", attrs);
    }

    @Override
    public Class javaTypeClass() {
        return Struct.class;
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
        return 0;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        for (int i = 0; i < nestedTypes.length; i++) {
            nestedTypes[i].serializeBinary(((Struct) data).getAttributes()[i], serializer);
        }
    }

    @Override
    public Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException {
        Object[] attrs = new Object[nestedTypes.length];
        for (int i = 0; i < nestedTypes.length; i++) {
            attrs[i] = nestedTypes[i].deserializeBinary(deserializer);
        }
        return new ClickHouseStruct("Tuple", attrs);
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        for (int i = 0; i < nestedTypes.length; i++) {
            Object[] elemsData = new Object[data.length];
            for (int row = 0; row < data.length; row++) {
                elemsData[row] = ((Struct) data[row]).getAttributes()[i];
            }
            nestedTypes[i].serializeBinaryBulk(elemsData, serializer);
        }
    }

    @Override
    public Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        Object[][] rowsWithElems = getRowsWithElems(rows, deserializer);

        Struct[] rowsData = new Struct[rows];
        for (int row = 0; row < rows; row++) {
            Object[] elemsData = new Object[nestedTypes.length];

            for (int elemIndex = 0; elemIndex < nestedTypes.length; elemIndex++) {
                elemsData[elemIndex] = rowsWithElems[elemIndex][row];
            }
            rowsData[row] = new ClickHouseStruct("Tuple", elemsData);
        }
        return rowsData;
    }

    private Object[][] getRowsWithElems(int rows, BinaryDeserializer deserializer) throws IOException, SQLException {
        Object[][] rowsWithElems = new Object[nestedTypes.length][];
        for (int index = 0; index < nestedTypes.length; index++) {
            rowsWithElems[index] = nestedTypes[index].deserializeBinaryBulk(rows, deserializer);
        }
        return rowsWithElems;
    }

    @Override
    public Object deserializeTextQuoted(SQLLexer lexer) throws SQLException {
        Validate.isTrue(lexer.character() == '(');
        Object[] tupleData = new Object[nestedTypes.length];
        for (int i = 0; i < nestedTypes.length; i++) {
            if (i > 0)
                Validate.isTrue(lexer.character() == ',');
            tupleData[i] = nestedTypes[i].deserializeTextQuoted(lexer);
        }
        Validate.isTrue(lexer.character() == ')');
        return new ClickHouseStruct("Tuple", tupleData);
    }
}
