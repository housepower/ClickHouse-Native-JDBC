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

import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;

// It would be nice if we introduce a Generic Type, `IDataType<T>`, then we can avoid using `Object` and type cast.
// Unfortunately Java not support unsigned number, UInt8(u_byte) must be represented by Int16(short), which will
// break the Generic Type constriction and cause compile failed.
public interface IDataType {

    String name();

    int sqlTypeId();

    Object defaultValue();

    Class javaTypeClass();

    boolean nullable();

    int getPrecision();

    int getScale();

    Object deserializeTextQuoted(SQLLexer lexer) throws SQLException;

    Object deserializeBinary(BinaryDeserializer deserializer) throws SQLException, IOException;

    void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException;

    default void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        for (Object d : data) {
            serializeBinary(d, serializer);
        }
    }

    Object[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws SQLException, IOException;
}
