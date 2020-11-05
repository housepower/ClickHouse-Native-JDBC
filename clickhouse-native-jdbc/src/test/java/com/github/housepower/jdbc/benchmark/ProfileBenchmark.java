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

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;

/**
 *
 */
public class ProfileBenchmark extends AbstractIBenchmark {
    private static final long n = 10000000;
    private static final long statsOutputFrequency = 10;

    public WithConnection benchSelectSumInt = connection -> {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(String.format(Locale.ROOT, "SELECT number as n1 from numbers(%d)", n));

        long sum = 0;
        long counter = 0;

        Profiler.getInstance().logStat(0, 0);
        while (rs.next()) {
            counter++;
            if (counter % (n / statsOutputFrequency) == 0) {
                long percentComplete = (long) ((((double) counter) / n) * 100);
                Profiler.getInstance().logStat(percentComplete, 1);
            }
            sum += rs.getLong(1);
        }
        System.out.println(sum);
    };

    public WithConnection benchSelectSumDouble = connection -> {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(String.format(Locale.ROOT, "SELECT number/rand64() as n1 from numbers(%d)", n));

        double sum = 0;
        long counter = 0;

        Profiler.getInstance().logStat(0, 0);
        while (rs.next()) {
            counter++;
            if (counter % (n / statsOutputFrequency) == 0) {
                long percentComplete = (long) ((((double) counter) / n) * 100);
                Profiler.getInstance().logStat(percentComplete, 1);
            }
            sum += rs.getDouble(1);
        }
        System.out.println(sum);
    };

    @Test
    public void profileSumIntNative() throws Exception {
        withConnection(benchSelectSumInt, ConnectionType.NATIVE);
    }

    @Test
    public void profileSumIntHttp() throws Exception {
        withConnection(benchSelectSumInt, ConnectionType.HTTP);
    }

    @Test
    public void profileSumDoubleNative() throws Exception {
        withConnection(benchSelectSumDouble, ConnectionType.NATIVE);
    }

    @Test
    public void profileSumDoubleHttp() throws Exception {
        withConnection(benchSelectSumDouble, ConnectionType.HTTP);
    }
}

