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
import java.math.RoundingMode;
import java.sql.*;

import joptsimple.internal.Strings;

import static org.junit.jupiter.api.Assertions.*;

public class DecimalTypeITest extends AbstractITest {

    @Test
    public void testDecimalType() throws Exception {
        withNewConnection(connection -> {
            BigDecimal value32 = BigDecimal.valueOf(1.32);
            value32 = value32.setScale(2, RoundingMode.HALF_UP);
            BigDecimal value64 = new BigDecimal("12343143412341.21");
            value64 = value64.setScale(5, RoundingMode.HALF_UP);

            BigDecimal value128 = new BigDecimal(Strings.repeat('1', (38 - 16)));
            value128 = value128.setScale(16, RoundingMode.HALF_UP);

            BigDecimal value256 = new BigDecimal(Strings.repeat('1', (76 - 26)));
            value256 = value256.setScale(26, RoundingMode.HALF_UP);

            BigDecimal[] valueArray = new BigDecimal[]{
                    BigDecimal.valueOf(412341.21D).setScale(3, RoundingMode.HALF_UP),
                    BigDecimal.valueOf(512341.25D).setScale(3, RoundingMode.HALF_UP)
            };
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS decimal_test");
            statement.execute("CREATE TABLE IF NOT EXISTS decimal_test (value32 Decimal(7,2), "
                              + "value64 Decimal(15,5), "
                              + "value128 Decimal(38, 16),"
                              + "value256 Decimal(76, 26),"
                              + "value_array Array(Decimal(5,3))) Engine=Memory()");
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO decimal_test"
                                                                  + "(value32,value64,value128,value256,value_array) "
                                                                  + "values(?,?,?,?,?);");
            for (int i = 0; i < 300; i++) {
                pstmt.setBigDecimal(1, value32);
                pstmt.setBigDecimal(2, value64);
                pstmt.setBigDecimal(3, value128);
                pstmt.setBigDecimal(4, value256);
                pstmt.setArray(5, connection.createArrayOf("Decimal(5,3)", valueArray));
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            // Check data count
            ResultSet rs = statement.executeQuery("SELECT * FROM decimal_test;");
            int size = 0;
            while (rs.next()) {
                size++;
                BigDecimal rsValue32 = rs.getBigDecimal(1);
                assertEquals(value32, rsValue32);
                BigDecimal rsValue64 = rs.getBigDecimal(2);
                assertEquals(value64, rsValue64);
                BigDecimal rsValue128 = rs.getBigDecimal(3);
                assertEquals(value128, rsValue128);
                BigDecimal rsValue256 = rs.getBigDecimal(4);
                assertEquals(value256, rsValue256);
                ClickHouseArray rsValueArray = (ClickHouseArray) rs.getArray(5);
                Object[] decimalArray = (Object[]) rsValueArray.getArray();
                assertEquals(decimalArray.length, valueArray.length);
                for (int i = 0; i < valueArray.length; i++) {
                    assertEquals(decimalArray[i], valueArray[i]);
                }
            }
            assertEquals(300, size);
            statement.execute("DROP TABLE IF EXISTS decimal_test");
        });
    }

}
