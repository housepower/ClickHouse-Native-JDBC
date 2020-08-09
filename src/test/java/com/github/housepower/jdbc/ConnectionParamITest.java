package com.github.housepower.jdbc;

import com.github.housepower.jdbc.settings.ClickHouseConfig;
import com.github.housepower.jdbc.settings.SettingKey;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;

public class ConnectionParamITest {

    @Before
    public void init() throws SQLException {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            DriverManager.deregisterDriver(drivers.nextElement());
        }
        DriverManager.registerDriver(new ClickHouseDriver());
    }

    @Test(expected = SQLException.class)
    public void successfullyMaxRowsToRead() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1?max_rows_to_read=1&connect_timeout=10");
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT arrayJoin([1,2,3,4]) from numbers(100)");
        int rowsRead = 0;
        while (rs.next()) {
            ++rowsRead;
        }
        Assert.assertEquals(1, rowsRead); // not reached
    }

    @Test
    public void successfullyMaxResultRows() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1?max_result_rows=1&connect_timeout=10");
        Statement statement = connection.createStatement();
        statement.setMaxRows(400);
        ResultSet rs = statement.executeQuery("SELECT arrayJoin([1,2,3,4]) from numbers(100)");
        int rowsRead = 0;
        while (rs.next()) {
            ++rowsRead;
        }
        Assert.assertEquals(400, rowsRead);
    }

    @Test
    public void successfullyUrlParser() throws Exception {
        String url = "jdbc:clickhouse://127.0.0.1/system?min_insert_block_size_rows=1000&connect_timeout=50";
        ClickHouseConfig config = new ClickHouseConfig(url , new Properties());
        Assert.assertEquals(config.database(), "system");
        Assert.assertEquals(config.settings().get(SettingKey.min_insert_block_size_rows), 1000L);

        Assert.assertEquals(config.connectTimeout(), 50000);
    }
}
