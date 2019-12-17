package com.github.housepower.jdbc.benchmark;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 */
public class SelectIBenchmark extends AbstractIBenchmark {

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    public WithConnection benchSelect = connection -> {
        long sum = 0;
        Statement statement = connection.createStatement();
        long number = 1000000;
        ResultSet rs = statement.executeQuery(String.format("SELECT number as n1, 'i_am_a_string' , now(), today() from numbers(%d)", number));
        while (rs.next()) {
            sum += rs.getInt(1);

            rs.getString(2);
            rs.getTimestamp(3);
            rs.getDate(4);
        }
        Assert.assertEquals((number-1) * number / 2, sum);
    };

    @BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 0)
    @Test
    public void benchSelectNative() throws Exception {
        withConnection(benchSelect, ConnectionType.NATIVE);
    }

    @BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 0)
    @Test
    public void benchSelectHTTP() throws Exception {
        withConnection(benchSelect, ConnectionType.HTTP);
    }
}
