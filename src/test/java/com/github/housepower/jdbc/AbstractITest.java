package com.github.housepower.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class AbstractITest {

    private static final int SERVER_PORT = Integer.valueOf(System.getProperty("CLICK_HOUSE_SERVER_PORT", "9000"));

    protected void withNewConnection(WithConnection withConnection, Object ...args) throws Exception {
        Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
        String connectionStr = "jdbc:clickhouse://127.0.0.1:" + SERVER_PORT;

        // first arg is use_client_time_zone
        if (args.length > 0) {
            if(args[0].equals(true)) {
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
