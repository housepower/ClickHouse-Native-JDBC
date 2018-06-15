package com.github.housepower.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class AbstractITest {

    private static final int SERVER_PORT = Integer.valueOf(System.getProperty("CLICK_HOUSE_SERVER_PORT", "9000"));

    protected void withNewConnection(WithConnection withConnection) throws Exception {
        Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:" + SERVER_PORT);

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
