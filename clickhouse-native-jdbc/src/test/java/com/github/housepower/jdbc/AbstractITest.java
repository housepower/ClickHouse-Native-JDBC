package com.github.housepower.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public abstract class AbstractITest {

    private static final int
        SERVER_PORT =
        Integer.valueOf(System.getProperty("CLICK_HOUSE_SERVER_PORT", "9000"));

    protected void withNewConnection(WithConnection withConnection, Object... args)
        throws Exception {
        // deregisterDriver other jdbc drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            DriverManager.deregisterDriver(drivers.nextElement());
        }
        DriverManager.registerDriver(new ClickHouseDriver());

        String connectionStr = "jdbc:clickhouse://127.0.0.1:" + SERVER_PORT;

        // first arg is use_client_time_zone
        if (args.length > 0) {
            if (args[0].equals(true)) {
                connectionStr += "?use_client_time_zone=true";
            }
        }
        Connection connection = DriverManager.getConnection(connectionStr);
        try {
            withConnection.apply(connection);
        } finally {
            connection.close();
        }
    }

    interface WithConnection {
        void apply(Connection connection) throws Exception;
    }
}
