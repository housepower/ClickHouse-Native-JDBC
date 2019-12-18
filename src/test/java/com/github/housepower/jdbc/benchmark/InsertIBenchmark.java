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

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class InsertIBenchmark extends AbstractIBenchmark{
    @Param({"10000", "100000", "1000000"})
    private int batchSize;
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

    public WithConnection benchInsert = new WithConnection(){
        @Override
        public void apply(Connection connection) throws Exception {
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            Date date = new Date(ts.getTime());

            Statement stmt = connection.createStatement();

            int tableId = tableMaxId.getAndIncrement();
            String testTable = "test_" + tableId;

            stmt.executeQuery("DROP TABLE IF EXISTS " + testTable);
            stmt.executeQuery("CREATE TABLE " + testTable +" (number UInt32, name String, birthTime DateTime, birthDay Date) Engine = Log");
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO "  + testTable +" values(?, ?, ?, ?)");


            for (int i = 0; i < batchSize; i++) {
                pstmt.setInt(1, i);
                pstmt.setString(2, "i_am_a_string");
                pstmt.setTimestamp(3, ts);
                pstmt.setDate(4, date);
                pstmt.addBatch();
            }
            int []res = pstmt.executeBatch();
            Assert.assertEquals(res.length, batchSize);
            stmt.executeQuery("DROP TABLE " + testTable);
        }
    };

    @Benchmark @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchInsertNative() throws Exception {
        withConnection(benchInsert, ConnectionType.NATIVE);
    }

    @Benchmark @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchInsertHttp() throws Exception {
        withConnection(benchInsert, ConnectionType.HTTP);
    }
}
