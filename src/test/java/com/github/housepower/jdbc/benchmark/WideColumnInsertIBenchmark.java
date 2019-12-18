package com.github.housepower.jdbc.benchmark;

import com.google.common.base.Strings;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */
public class WideColumnInsertIBenchmark extends AbstractIBenchmark{
    private static final int COLUMN_NUM = 150;
    private static final int BATCH_SZIE = 200000;

    AtomicInteger tableMaxId = new AtomicInteger();

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    public WithConnection benchInsert = new WithConnection(){
        @Override
        public void apply(Connection connection) throws Exception {
            Statement stmt = connection.createStatement();

            int tableId = tableMaxId.getAndIncrement();
            String testTable = "test2_" + tableId;


            stmt.executeQuery("DROP TABLE IF EXISTS " + testTable);
            String createSQL = "CREATE TABLE " + testTable + " (";
            for (int i = 0; i < COLUMN_NUM; i++) {
                createSQL += "col_" + i + " String";
                if (i + 1 != COLUMN_NUM) {
                    createSQL += ",\n";
                }
            }
            createSQL += ") Engine = Log";

            stmt.executeQuery(createSQL);
            String params = Strings.repeat("?, ", COLUMN_NUM);
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO "  + testTable +" values("   + params.substring(0, params.length()-2) + ")");


            for (int i = 0; i < BATCH_SZIE; i++) {
                for (int j = 0; j < COLUMN_NUM; j++ ) {
                    pstmt.setString(j + 1, j + 1 + "");
                }
                pstmt.addBatch();
            }
            int []res = pstmt.executeBatch();
            Assert.assertEquals(res.length, BATCH_SZIE);
            stmt.executeQuery("DROP TABLE " + testTable);
        }
    };

    @BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
    @Test
    public void benchInsertNative() throws Exception {
        withConnection(benchInsert, ConnectionType.NATIVE);
    }

    @BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
    @Test
    public void benchInsertHttp() throws Exception {
        withConnection(benchInsert, ConnectionType.HTTP);
    }
}
