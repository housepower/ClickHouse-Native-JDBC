package org.houseflys.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class AbstractITest {

    private static final int SERVER_PORT = Integer.parseInt(System.getProperty("CLICK_HOUSE_SERVER_PORT"));

    protected void withNewConnection(WithConnection withConnection) throws Exception {
        Class.forName("org.houseflys.jdbc.ClickHouseDriver");
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
