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

package com.github.housepower.jdbc.data;

import com.github.housepower.exception.NoDefaultValueException;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;

public interface IDataType<CK, JDBC> {

    String name();

    int sqlTypeId();

    // TODO detect column default value from sample block
    default CK defaultValue() {
        throw new NoDefaultValueException("Column[" + name() + "] doesn't has default value");
    }

    Class<CK> javaType();

    @SuppressWarnings("unchecked")
    default Class<JDBC> jdbcJavaType() {
        return (Class<JDBC>) javaType();
    }

    default boolean nullable() {
        return false;
    }

    int getPrecision();

    int getScale();

    CK deserializeTextQuoted(SQLLexer lexer) throws SQLException;

    CK deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException;

    void serializeBinary(CK data, BinarySerializer serializer) throws SQLException, IOException;

    default void serializeBinaryBulk(CK[] data, BinarySerializer serializer) throws SQLException, IOException {
        for (CK d : data) {
            serializeBinary(d, serializer);
        }
    }

    @SuppressWarnings("unchecked")
    default CK[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException {
        CK[] data = (CK[]) new Object[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = this.deserializeBinary(deserializer);
        }
        return data;
    }

    default String[] getAliases() {
        return new String[0];
    }

    default boolean isSigned() {
        return false;
    }
}
