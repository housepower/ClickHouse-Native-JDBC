package com.github.housepower.jdbc.benchmark;

import org.junit.Assert;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;

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
        Assert.assertEquals((selectNumber-1) * selectNumber / 2, sum);
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
