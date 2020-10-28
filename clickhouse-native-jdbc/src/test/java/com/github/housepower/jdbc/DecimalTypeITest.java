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

import static org.junit.jupiter.api.Assertions.*;

public class DecimalTypeITest extends AbstractITest {

    @Test
    public void testDecimalType() throws Exception {
        withNewConnection(connection -> {
            BigDecimal value32 = BigDecimal.valueOf(1.32);
            value32 = value32.setScale(2, RoundingMode.HALF_UP);
            BigDecimal value64 = BigDecimal.valueOf(12343143412341.21D);
            value64 = value64.setScale(5, RoundingMode.HALF_UP);

            BigDecimal[] valueArray = new BigDecimal[]{
                    BigDecimal.valueOf(412341.21D).setScale(3, RoundingMode.HALF_UP),
                    BigDecimal.valueOf(512341.25D).setScale(3, RoundingMode.HALF_UP)
            };
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS decimal_test");
            statement.execute("CREATE TABLE IF NOT EXISTS decimal_test (value32 Decimal(7,2), value64 Decimal(15,5), value_array Array(Decimal(5,3))) Engine=Memory();");
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO decimal_test(value32,value64,value_array) values(?,?,?);");
            for (int i = 0; i < 3; i++) {
                pstmt.setBigDecimal(1, value32);
                pstmt.setBigDecimal(2, value64);
                pstmt.setArray(3, new ClickHouseArray(valueArray));
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
                ClickHouseArray rsValueArray = (ClickHouseArray) rs.getArray(3);
                Object[] decimalArray = (Object[]) rsValueArray.getArray();
                assertEquals(decimalArray.length, valueArray.length);
                for (int i = 0; i < valueArray.length; i++) {
                    assertEquals(decimalArray[i], valueArray[i]);
                }
            }
            assertEquals(3, size);
            statement.execute("DROP TABLE IF EXISTS decimal_test");
        });
    }

}
