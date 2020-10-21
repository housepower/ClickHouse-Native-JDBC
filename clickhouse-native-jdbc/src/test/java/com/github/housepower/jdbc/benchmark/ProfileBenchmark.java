package com.github.housepower.jdbc.benchmark;

/**
 */

import org.junit.Test;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;

/**
 */
public class ProfileBenchmark extends AbstractIBenchmark{
    private static long n = 10000000;
    private static long statsOutputFrequency = 10;

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

