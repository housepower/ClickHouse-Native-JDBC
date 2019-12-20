package com.github.housepower.jdbc.benchmark;

import com.google.common.base.Strings;

import org.junit.Assert;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 */
public class WideColumnDoubleInsertIBenchmark extends AbstractIBenchmark{
    @Benchmark
    @Test
    public void benchInsertNative() throws Exception {
        withConnection(benchInsert, ConnectionType.NATIVE);
    }

    @Benchmark
    @Test
    public void benchInsertHttp() throws Exception {
        withConnection(benchInsert, ConnectionType.HTTP);
    }

    public WithConnection benchInsert = new WithConnection(){
        @Override
        public void apply(Connection connection) throws Exception {
            wideColumnPrepare(connection, "Float64");

            String params = Strings.repeat("?, ", columnNum);
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO "  + getTableName() +" values("   + params.substring(0, params.length()-2) + ")");


            for (int i = 0; i < batchSize; i++) {
                for (int j = 0; j < columnNum; j++ ) {
                    pstmt.setDouble(j + 1, j + 1.2);
                }
                pstmt.addBatch();
            }
            int []res = pstmt.executeBatch();
            Assert.assertEquals(res.length, batchSize);

            wideColumnAfter(connection);
        }
    };

}
