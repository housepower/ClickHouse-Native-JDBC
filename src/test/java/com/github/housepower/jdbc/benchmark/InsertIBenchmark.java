package com.github.housepower.jdbc.benchmark;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */
public class InsertIBenchmark extends AbstractIBenchmark{
    AtomicInteger tableMaxId = new AtomicInteger();

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    public WithConnection benchInsert = new WithConnection(){
        @Override
        public void apply(Connection connection) throws Exception {
            int batchSize = 1000000;
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

    @BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 0, concurrency = 1)
    @Test
    public void benchInsertNative() throws Exception {
        withConnection(benchInsert, ConnectionType.NATIVE);
    }

    @BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 0, concurrency = 1)
    @Test
    public void benchInsertHttp() throws Exception {
        withConnection(benchInsert, ConnectionType.HTTP);
    }
}
