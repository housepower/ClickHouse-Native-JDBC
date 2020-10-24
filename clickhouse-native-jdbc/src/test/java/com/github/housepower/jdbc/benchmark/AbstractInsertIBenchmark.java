package com.github.housepower.jdbc.benchmark;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class AbstractInsertIBenchmark extends AbstractIBenchmark{
    @Param({"20", "50"})
    protected int columnNum = 20;
    @Param({"200000", "500000"})
    protected int batchSize = 200000;

    AtomicInteger tableMaxId = new AtomicInteger();

    //drop table, create table
    protected void wideColumnPrepare(Connection connection, String columnType) throws SQLException {
        int tableId = tableMaxId.incrementAndGet();
        String testTable = "test_" + tableId;
        Statement stmt = connection.createStatement();
        stmt.executeQuery("DROP TABLE IF EXISTS " + testTable);
        StringBuilder createSQL = new StringBuilder("CREATE TABLE " + testTable + " (");
        for (int i = 0; i < columnNum; i++) {
            createSQL.append("col_").append(i).append(" ").append(columnType);
            if (i + 1 != columnNum) {
                createSQL.append(",\n");
            }
        }
        stmt.executeQuery(createSQL + ")Engine = Log");
    }

    protected void wideColumnAfter(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeQuery("DROP TABLE " + getTableName());
    }

    protected String getTableName() {
        return "test_" + tableMaxId.get();
    }
}
