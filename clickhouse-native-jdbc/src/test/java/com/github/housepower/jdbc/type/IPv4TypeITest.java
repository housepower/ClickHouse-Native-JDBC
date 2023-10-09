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

public class IPv4TypeITest extends AbstractITest implements BytesHelper {

    @Test
    public void testIPv4Type() throws Exception {
        withStatement(statement -> {
            statement.execute("DROP TABLE IF EXISTS ipv4_test");
            statement.execute("CREATE TABLE IF NOT EXISTS ipv4_test (value IPv4, nullableValue Nullable(IPv4)) Engine=Memory()");

            Integer rowCnt = 300;
            Long testIPv4Value1 = ipToLong("192.168.1.1");
            Long testIPv4Value2 = ipToLong("127.0.0.1");

            try (PreparedStatement pstmt = statement.getConnection().prepareStatement(
                    "INSERT INTO ipv4_test (value, nullableValue) values(?, ?);")) {
                for (int i = 0; i < rowCnt; i++) {
                    pstmt.setLong(1, testIPv4Value1);
                    pstmt.setLong(2, testIPv4Value2);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            ResultSet rs = statement.executeQuery("SELECT * FROM ipv4_test;");
            int size = 0;
            while (rs.next()) {
                size++;
                Long value = rs.getLong(1);
                assertEquals(value, testIPv4Value1);
                Long nullableValue = rs.getLong(2);
                assertEquals(nullableValue, testIPv4Value2);
            }

            assertEquals(size, (int) rowCnt);

            statement.execute("DROP TABLE IF EXISTS ipv4_test");
        });
    }

    public long ipToLong(String ipAddress) {
        String[] ipAddressInArray = ipAddress.split("\\.");

        long result = 0;
        for (int i = 0; i < ipAddressInArray.length; i++) {
            int power = 3 - i;
            int ip = Integer.parseInt(ipAddressInArray[i]);
            result += ip * Math.pow(256, power);
        }

        return result;
    }

}
