package com.github.housepower.jdbc;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionParamITest {

    @Test(expected = SQLException.class)
    public void successfullyMaxRowsToRead() throws Exception {
        Class.forName("org.houseflys.jdbc.ClickHouseDriver");
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1?max_rows_to_read=1");
        Statement statement = connection.createStatement();
        statement.execute("SELECT 1 UNION ALL SELECT 2");
    }
}
