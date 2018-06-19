package com.github.housepower.jdbc.benchmark;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;


public abstract class AbstractIBenchmark {

    private static final int SERVER_PORT = Integer.valueOf(System.getProperty("CLICK_HOUSE_SERVER_PORT", "9000"));
    private static final int SERVER_HTTP_PORT = Integer.valueOf(System.getProperty("CLICK_HOUSE_SERVER_HTTP_PORT", "8123"));

    private final Driver httpDriver = new ru.yandex.clickhouse.ClickHouseDriver();
    private final Driver nativeDriver = new com.github.housepower.jdbc.ClickHouseDriver();

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
}
