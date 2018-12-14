package com.github.housepower.jdbc;

import com.github.housepower.jdbc.settings.ClickHouseConfig;
import com.github.housepower.jdbc.settings.SettingKey;

import org.junit.Assert;
import org.junit.Test;

import java.sql.*;
import java.util.Properties;

public class ConnectionParamITest {

    @Test(expected = SQLException.class)
    public void successfullyMaxRowsToRead() throws Exception {
        Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1?max_rows_to_read=1&connect_timeout=10");
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT 1 UNION ALL SELECT 2");
        int rowsRead = 0;
        while (rs.next()) {
            ++rowsRead;
        }
        Assert.assertEquals(1, rowsRead); // not reached
    }

    @Test
    public void successfullyUrlParser() throws Exception {
        Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
        String url = "jdbc:clickhouse://127.0.0.1/system?min_insert_block_size_rows=1000&connect_timeout=50";
        ClickHouseConfig config = new ClickHouseConfig(url , new Properties());
        Assert.assertEquals(config.database(), "system");
        Assert.assertEquals(config.settings().get(SettingKey.min_insert_block_size_rows), 1000L);

        Assert.assertEquals(config.connectTimeout(), 50);
    }
}
