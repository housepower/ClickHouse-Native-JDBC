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

package com.github.housepower.jdbc.benchmark;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 */
public class SelectIBenchmark extends AbstractIBenchmark {
    @Param({"500000", "10000000"})
    protected long selectNumber = 100000;
    public WithConnection benchSelect = connection -> {
        long sum = 0;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(String.format(Locale.ROOT, "SELECT number as n1, 'i_am_a_string' , now(), today() from numbers(%d)", selectNumber));
        while (rs.next()) {
            sum += rs.getLong(1);

            rs.getString(2);
            rs.getTimestamp(3);
            rs.getDate(4);
        }
        assertEquals((selectNumber-1) * selectNumber / 2, sum);
    };

    @Benchmark
    @Test
    public void benchSelectNative() throws Exception {
        withConnection(benchSelect, ConnectionType.NATIVE);
    }

    @Benchmark
    @Test
    public void benchSelectHTTP() throws Exception {
        withConnection(benchSelect, ConnectionType.HTTP);
    }
}
