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

import static org.junit.jupiter.api.Assertions.*;

class ClickHouseResultSetBuilderITest extends AbstractITest {

    @Test
    public void testBuildEmptyResultSet() throws Exception {
        withNewConnection(connection -> {
            ClickHouseResultSet rs = ClickHouseResultSetBuilder
                    .builder(1, ((ClickHouseConnection) connection).serverContext())
                    .cfg(((ClickHouseConnection) connection).cfg())
                    .columnNames("some")
                    .columnTypes("String")
                    .build();
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertEquals("some", rs.getMetaData().getColumnName(1));
            assertFalse(rs.next());
        });
    }

    @Test
    public void testBuildResultSetWithRow() throws Exception {
        withNewConnection(connection -> {
            ClickHouseResultSet rs = ClickHouseResultSetBuilder
                    .builder(1, ((ClickHouseConnection) connection).serverContext())
                    .cfg(((ClickHouseConnection) connection).cfg())
                    .columnNames("some")
                    .columnTypes("String")
                    .addRow("A")
                    .addRow("B")
                    .build();
            assertTrue(rs.next());
            assertTrue(rs.next());
            assertFalse(rs.next());
        });
    }
}
