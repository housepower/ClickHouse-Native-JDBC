package com.github.housepower.jdbc.benchmark;

import com.google.common.base.Strings;

import org.junit.Assert;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

import ru.yandex.clickhouse.ClickHouseStatement;
import ru.yandex.clickhouse.domain.ClickHouseFormat;
import ru.yandex.clickhouse.util.ClickHouseRowBinaryStream;
import ru.yandex.clickhouse.util.ClickHouseStreamCallback;

/**
 */
public class RowBinaryIBenchmark extends AbstractIBenchmark{
    AtomicInteger tableMaxId = new AtomicInteger();


    @Benchmark
    @Test
    public void benchInsertNative() throws Exception {
        withConnection(benchInsert, ConnectionType.NATIVE);
    }

    @Benchmark
    @Test
    public void benchInsertHttpRowBinary() throws Exception {
        withConnection(new WithConnection() {
            @Override
            public void apply(Connection connection) throws Exception {
                ClickHouseStatement sth = (ClickHouseStatement) connection.createStatement();
                int tableId = tableMaxId.getAndIncrement();
                String testTable = "test2_" + tableId;
                ;
                Statement stmt = connection.createStatement();
                stmt.executeQuery("DROP TABLE IF EXISTS " + testTable);
                String createSQL = "CREATE TABLE " + testTable + " (";
                for (int i = 0; i < columnNum; i++) {
                    createSQL += "col_" + i + " UInt32";
                    if (i + 1 != columnNum) {
                        createSQL += ",\n";
                    }
                }
                createSQL += ") Engine = Log";
                stmt.executeQuery(createSQL);
                sth.write().send("INSERT INTO " + testTable, new ClickHouseStreamCallback() {
                                     @Override
                                     public void writeTo(ClickHouseRowBinaryStream stream) throws
                                                                                           IOException {
                                         for (int i = 0; i < batchSize; i++) {
                                             for (int j = 0; j < columnNum; j++ ) {
                                                 stream.writeInt32(j + 1);
                                             }
                                         }
                                     }
                                 },
                                 ClickHouseFormat.RowBinary);
            }
        }, ConnectionType.HTTP);
    }

    public WithConnection benchInsert = new WithConnection(){
        @Override
        public void apply(Connection connection) throws Exception {
            Statement stmt = connection.createStatement();

            int tableId = tableMaxId.getAndIncrement();
            String testTable = "test2_" + tableId;


            stmt.executeQuery("DROP TABLE IF EXISTS " + testTable);
            String createSQL = "CREATE TABLE " + testTable + " (";
            for (int i = 0; i < columnNum; i++) {
                createSQL += "col_" + i + " UInt32";
                if (i + 1 != columnNum) {
                    createSQL += ",\n";
                }
            }
            createSQL += ") Engine = Log";

            stmt.executeQuery(createSQL);
            String params = Strings.repeat("?, ", columnNum);
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO "  + testTable +" values("   + params.substring(0, params.length()-2) + ")");


            for (int i = 0; i < batchSize; i++) {
                for (int j = 0; j < columnNum; j++ ) {
                    pstmt.setInt(j + 1, j + 1);
                }
                pstmt.addBatch();
            }
            int []res = pstmt.executeBatch();
            Assert.assertEquals(res.length, batchSize);
            stmt.executeQuery("DROP TABLE " + testTable);
        }
    };

}
