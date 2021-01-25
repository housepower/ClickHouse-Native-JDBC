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

import com.github.housepower.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BalancedClickhouseDataSourceTest {

    @Test
    public void testUrlSplit() {
        assertEquals(Collections.singletonList("jdbc:clickhouse://localhost:1234/ppc"),
                BalancedClickhouseDataSource.splitUrl("jdbc:clickhouse://localhost:1234/ppc"));

        assertEquals(Arrays.asList("jdbc:clickhouse://localhost:1234/ppc",
                "jdbc:clickhouse://another.host.com:4321/ppc"),
                BalancedClickhouseDataSource.splitUrl(
                        "jdbc:clickhouse://localhost:1234,another.host.com:4321/ppc"));

        assertEquals(Arrays.asList("jdbc:clickhouse://localhost:1234", "jdbc:clickhouse://another.host.com:4321"),
                BalancedClickhouseDataSource.splitUrl(
                        "jdbc:clickhouse://localhost:1234,another.host.com:4321"));
    }

    @Test
    public void testUrlSplitValidHostName() {
        assertEquals(Arrays.asList("jdbc:clickhouse://localhost:1234", "jdbc:clickhouse://_0another-host.com:4321"),
                BalancedClickhouseDataSource.splitUrl("jdbc:clickhouse://localhost:1234,_0another-host.com:4321"));
    }

    @Test
    public void testUrlSplitInvalidHostName() {
        assertThrows(InvalidValueException.class, () ->
                BalancedClickhouseDataSource.splitUrl("jdbc:clickhouse://localhost:1234,_0ano^ther-host.com:4321"));
    }
}
