package com.github.housepower.jdbc.benchmark;

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
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class AbstractIBenchmark {
    @Param({"100000", "500000", "10000000"})
    protected long selectNumber = 100000;
    @Param({"20", "50", "100"})
    protected int columnNum = 20;
    @Param({"200000", "500000"})
    protected int batchSize = 200000;

    AtomicInteger tableMaxId = new AtomicInteger();

    private static final int SERVER_PORT = Integer.valueOf(System.getProperty("CLICK_HOUSE_SERVER_PORT", "9000"));
    private static final int SERVER_HTTP_PORT = Integer.valueOf(System.getProperty("CLICK_HOUSE_SERVER_HTTP_PORT", "8123"));

    private final Driver httpDriver = new ru.yandex.clickhouse.ClickHouseDriver();
    private final Driver nativeDriver = new com.github.housepower.jdbc.ClickHouseDriver();


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                          .include(".*")
                          .warmupIterations(0)
                          .measurementIterations(1)
                          .forks(2)
                          .build();

        new Runner(opt).run();
    }

    protected void withConnection(WithConnection withConnection, ConnectionType connectionType) throws Exception {
        int port = SERVER_PORT;

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            DriverManager.deregisterDriver(drivers.nextElement());
        }

        switch (connectionType) {
            case HTTP:
                Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
                DriverManager.registerDriver(httpDriver);
                port = SERVER_HTTP_PORT;
                break;

            case NATIVE:
                Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
                DriverManager.registerDriver(nativeDriver);
                break;
        }
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:" + port);
        try {
            withConnection.apply(connection);
        } finally {
            connection.close();
        }
    }


    public interface WithConnection {
        void apply(Connection connection) throws Exception;
    }

    public enum ConnectionType {
        NATIVE, HTTP
    }


    //drop table, create table
    protected void wideColumnPrepare(Connection connection, String columnType) throws SQLException {
        int tableId = tableMaxId.incrementAndGet();
        String testTable = "test_" + tableId;
        Statement stmt = connection.createStatement();
        stmt.executeQuery("DROP TABLE IF EXISTS " + testTable);
        String createSQL = "CREATE TABLE " + testTable + " (";
        for (int i = 0; i < columnNum; i++) {
            createSQL += "col_" + i + " "  + columnType ;
            if (i + 1 != columnNum) {
                createSQL += ",\n";
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
