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

package com.github.housepower.jdbc.type;

import com.github.housepower.jdbc.AbstractITest;
import com.github.housepower.misc.BytesHelper;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BooleanTypeITest extends AbstractITest implements BytesHelper {

    @Test
    public void testBooleanType() throws Exception {
        withStatement(statement -> {
            statement.execute("DROP TABLE IF EXISTS bool_test");
            statement.execute("CREATE TABLE IF NOT EXISTS bool_test (value Bool, nullableValue Nullable(Bool)) Engine=Memory()");

            Integer rowCnt = 300;
            try (PreparedStatement pstmt = statement.getConnection().prepareStatement(
                    "INSERT INTO bool_test (value, nullableValue) values(?, ?);")) {
                for (int i = 0; i < rowCnt; i++) {
                    pstmt.setBoolean(1, Boolean.TRUE);
                    pstmt.setBoolean(2, Boolean.FALSE);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            ResultSet rs = statement.executeQuery("SELECT * FROM bool_test;");
            int size = 0;
            while (rs.next()) {
                size++;
                Boolean value = rs.getBoolean(1);
                assertEquals(value, Boolean.TRUE);
                Boolean nullableValue = rs.getBoolean(2);
                assertEquals(nullableValue, Boolean.FALSE);
            }

            assertEquals(size, rowCnt);

            statement.execute("DROP TABLE IF EXISTS bool_test");
        });

    }


}
