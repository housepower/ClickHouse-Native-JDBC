package com.github.housepower.jdbc;

import org.junit.Test;

import java.math.BigDecimal;
import java.sql.*;

import static org.junit.Assert.assertEquals;

public class DecimalTypeTest extends AbstractITest {

    @Test
    public void testDecimalType() throws Exception {
        withNewConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                BigDecimal value32 = BigDecimal.valueOf(1.32);
                value32 = value32.setScale(2);
                BigDecimal value64 = BigDecimal.valueOf(12343143412341.21D);
                value64 = value64.setScale(5);

                BigDecimal[] valueArray = new BigDecimal[]{BigDecimal.valueOf(412341.21D).setScale(3), BigDecimal.valueOf(512341.25D).setScale(3)};
                Statement statement = connection.createStatement();
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
                    Object v = rsValueArray.getArray();
                    Object[] decimalArray = (Object[]) rsValueArray.getArray();
                    assertEquals(decimalArray.length, valueArray.length);
                    for (int i = 0; i < valueArray.length; i++) {
                        assertEquals(decimalArray[i], valueArray[i]);
                    }
                }
                assertEquals(size, 3);
                statement.execute("DROP TABLE IF EXISTS decimal_test");
            }
        });
    }

}
