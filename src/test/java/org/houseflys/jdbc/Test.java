package org.houseflys.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class Test {


    @org.junit.Test
    public void test() throws Exception {
        Class.forName("org.houseflys.jdbc.ClickHouseDriver");

        Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000");

        ResultSet resultSet = connection.createStatement()
            .executeQuery("SELECT sumState(1)");

        while (resultSet.next()) {
            System.out.println(resultSet.getByte(1) + "\t" + resultSet.getString(2));
        }
    }
}
