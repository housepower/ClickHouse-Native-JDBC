package com.github.housepower.jdbc.benchmark;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class AbstractIBenchmark {
    private static final int
        SERVER_PORT = Integer.parseInt(System.getProperty("CLICK_HOUSE_SERVER_PORT", "9000"));
    private static final int
        SERVER_HTTP_PORT = Integer.parseInt(System.getProperty("CLICK_HOUSE_SERVER_HTTP_PORT", "8123"));

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

    protected void withConnection(WithConnection withConnection, ConnectionType connectionType)
        throws Exception {
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
        try (Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:" + port)) {
            withConnection.apply(connection);
        }
    }


    public interface WithConnection {

        void apply(Connection connection) throws Exception;
    }

    public enum ConnectionType {
        NATIVE, HTTP
    }

}