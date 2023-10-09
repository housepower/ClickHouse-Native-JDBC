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
import com.github.housepower.jdbc.ClickHouseResultSet;
import com.github.housepower.misc.BytesHelper;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.Types;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IPv6TypeITest extends AbstractITest implements BytesHelper {

    @Test
    public void testIPv6Type() throws Exception {
        withStatement(statement -> {
            statement.execute("DROP TABLE IF EXISTS ipv6_test");
            statement.execute("CREATE TABLE IF NOT EXISTS ipv6_test (value IPv6, nullableValue Nullable(IPv6)) Engine=Memory()");

            Integer rowCnt = 300;

            BigInteger testIPv6Value1 = new BigInteger("20010db885a3000000008a2e03707334", 16);
            BigInteger testIPv6Value2 = new BigInteger("1", 16);

            try (PreparedStatement pstmt = statement.getConnection().prepareStatement(
                    "INSERT INTO ipv6_test (value, nullableValue) values(?, ?);")) {
                for (int i = 0; i < rowCnt; i++) {
                    pstmt.setObject(1, testIPv6Value1, Types.BIGINT);
                    pstmt.setObject(2, testIPv6Value2, Types.BIGINT);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            ClickHouseResultSet rs = (ClickHouseResultSet) statement.executeQuery("SELECT * FROM ipv6_test;");
            int size = 0;
            while (rs.next()) {
                size++;
                BigInteger value = rs.getBigInteger(1);
                assertEquals(value, testIPv6Value1);
                BigInteger nullableValue = rs.getBigInteger(2);
                assertEquals(nullableValue, testIPv6Value2);
            }

            assertEquals(size, (int) rowCnt);

            statement.execute("DROP TABLE IF EXISTS ipv6_test");
        });
    }

}
