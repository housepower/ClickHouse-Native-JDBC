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

package com.github.housepower.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultSetMetadataITest extends AbstractITest {

    @Test
    public void successfullyMetaData() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(a UInt8, b UInt64, c FixedString(3) )ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES (1, 2, '4' )");
            ResultSet rs = statement.executeQuery("SELECT * FROM test");
            ResultSetMetaData metadata = rs.getMetaData();

            assertEquals("test", metadata.getTableName(1));
            assertEquals("default", metadata.getCatalogName(1));

            assertEquals(3, metadata.getPrecision(1));
            assertEquals(19, metadata.getPrecision(2));
            assertEquals(3, metadata.getPrecision(3));
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }

}
