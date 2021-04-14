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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryRandomITest extends AbstractITest {

    @Test
    public void successfullyDateTime64DataType() throws Exception {
        withStatement(statement -> {
            statement.executeQuery("DROP TABLE IF EXISTS test_random");
            statement.executeQuery("CREATE TABLE test_random "
                                   + "(name String, value UInt32, arr Array(Float64), day Date, time DateTime, dc Decimal(7,2))"
                                   + "ENGINE = GenerateRandom(1, 8, 8)");

            ResultSet rs = statement.executeQuery("SELECT * FROM test_random limit 10000");

            int i = 0;
            while (rs.next()) {
                Object name = rs.getObject(1);
                Object value = rs.getObject(2);
                Object arr = rs.getObject(3);
                Object day = rs.getObject(4);
                Object time = rs.getObject(5);
                Object dc = rs.getObject(6);

                assertEquals(String.class, name.getClass());
                assertEquals(Long.class, value.getClass());
                assertEquals(ClickHouseArray.class, arr.getClass());
                assertEquals(Date.class, day.getClass());
                assertEquals(Timestamp.class, time.getClass());
                assertEquals(BigDecimal.class, dc.getClass());

                i ++;
            }
            assertEquals(i , 10000);
            statement.executeQuery("DROP TABLE IF EXISTS test_random");
        }, "use_client_time_zone", true);
    }
}
