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

import com.github.housepower.annotation.Issue;
import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IssueReproduceITest extends AbstractITest {

    @Test
    @Issue("63")
    public void testIssue63() throws Exception {
        withStatement(statement -> {
            int columnNum = 36;
            statement.executeQuery("DROP TABLE IF EXISTS test");
            String params = Strings.repeat("?, ", columnNum);
            StringBuilder columnTypes = new StringBuilder();
            for (int i = 0; i < columnNum; i++) {
                if (i != 0) {
                    columnTypes.append(", ");
                }
                columnTypes.append("t_").append(i).append(" String");
            }
            statement.executeQuery("CREATE TABLE test( " + columnTypes + ")ENGINE=Log");

            withPreparedStatement(statement.getConnection(), "INSERT INTO test values(" + params.substring(0, params.length() - 2) + ")", pstmt -> {
                for (int i = 0; i < 100; ++i) {
                    for (int j = 0; j < columnNum; j++) {
                        pstmt.setString(j + 1, "String" + j);
                    }
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            });

            ResultSet rs = statement.executeQuery("SELECT count(1) FROM test limit 1");
            assertTrue(rs.next());
            assertEquals(100, rs.getInt(1));
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }
}
