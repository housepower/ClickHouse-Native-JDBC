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

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

//Refer to [[https://github.com/housepower/ClickHouse-Native-JDBC/issues/442]] for more details.
public class LowCardinalityTypeTest extends AbstractITest implements BytesHelper {
    @Test
    public void testAllLowCardinalityTypes() throws Exception {
        withStatement(statement -> {
            statement.execute("DROP TABLE IF EXISTS low_cardinality_test");

            StringBuilder createTableSQL = new StringBuilder();
            createTableSQL.append("CREATE TABLE IF NOT EXISTS low_cardinality_test (")
                    .append("value_string LowCardinality(String), ")
                    .append("fixed_string LowCardinality(FixedString(10)), ")
                    .append("date_value LowCardinality(Nullable(Date)), ")
                    .append("datetime_value LowCardinality(Nullable(DateTime)), ")
                    .append("number_value LowCardinality(Nullable(Int32))) Engine=Memory()");

            statement.execute(createTableSQL.toString());

            String sql = "INSERT INTO low_cardinality_test " +
                    "(value_string, fixed_string, date_value, datetime_value, number_value) values(?, ?, ?, ?, ?);";

            try (PreparedStatement pstmt = statement.getConnection().prepareStatement(sql)) {
                for (int i = 0; i < 300; i++) {
                    pstmt.setString(1, "test");
                    pstmt.setString(2, "abcdefghij");
                    if (i % 50 == 0) {
                        pstmt.setNull(3, Types.DATE);
                        pstmt.setNull(4, Types.TIMESTAMP);
                        pstmt.setNull(5, Types.INTEGER);
                    } else {
                        pstmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                        pstmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
                        pstmt.setInt(5, i);
                    }
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            DatabaseMetaData metaData = statement.getConnection().getMetaData();
            ResultSet columns = metaData.getColumns(null, "default", "low_cardinality_test", "%");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                switch (columnName) {
                    case "value_string":
                        assertEquals(columnType, "LowCardinality(String)");
                        break;
                    case "fixed_string":
                        assertEquals(columnType, "LowCardinality(FixedString(10))");
                        break;
                    case "date_value":
                        assertEquals(columnType, "LowCardinality(Nullable(Date))");
                        break;
                    case "datetime_value":
                        assertEquals(columnType, "LowCardinality(Nullable(DateTime))");
                        break;
                    case "number_value":
                        assertEquals(columnType, "LowCardinality(Nullable(Int32))");
                        break;
                }
            }

            ResultSet rs = statement.executeQuery("SELECT * FROM low_cardinality_test;");
            int size = 0;
            while (rs.next()) {
                String valueStr = rs.getString("value_string");
                String fixedStr = rs.getString("fixed_string");
                Date dateValue = rs.getDate("date_value");
                Timestamp datetimeValue = rs.getTimestamp("datetime_value");
                Integer numberValue = (Integer) rs.getObject("number_value");

                assertEquals("test", valueStr);
                assertEquals("abcdefghij", fixedStr);
                if (size % 50 == 0) {
                    assertNull(dateValue);
                    assertNull(datetimeValue);
                    assertNull(numberValue);
                } else {
                    assertNotNull(dateValue);
                    assertNotNull(datetimeValue);
                    assertTrue(numberValue >= 0 && numberValue < 300);
                }

                size++;
            }
            assertEquals(300, size);

            statement.execute("DROP TABLE IF EXISTS low_cardinality_test");
        }, "allow_suspicious_low_cardinality_types", "1");
    }
}
