package com.github.housepower.jdbc.benchmark;

import org.junit.Assert;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class SelectIBenchmark extends AbstractIBenchmark {
    @Param({"10000"})
    private int number;
    AtomicInteger tableMaxId = new AtomicInteger();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                          .include(WideColumnDoubleInsertIBenchmark.class.getSimpleName())
                          .warmupIterations(0)
                          .measurementIterations(3)
                          .forks(1)
                          .build();

        new Runner(opt).run();
    }
    public WithConnection benchSelect = connection -> {
        long sum = 0;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(String.format("SELECT number as n1, 'i_am_a_string' , now(), today() from numbers(%d)", number));
        while (rs.next()) {
            sum += rs.getInt(1);

            rs.getString(2);
            rs.getTimestamp(3);
            rs.getDate(4);
        }
        Assert.assertEquals((number-1) * number / 2, sum);
    };

    @Benchmark
    public void benchSelectNative() throws Exception {
        withConnection(benchSelect, ConnectionType.NATIVE);
    }

    @Benchmark
    public void benchSelectHTTP() throws Exception {
        withConnection(benchSelect, ConnectionType.HTTP);
    }
}
