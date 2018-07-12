package com.github.housepower.jdbc;

import com.github.housepower.jdbc.settings.ClickHouseConfig;
import com.github.housepower.jdbc.settings.SettingKey;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConnectionParamITest {

    @Test(expected = SQLException.class)
    public void successfullyMaxRowsToRead() throws Exception {
        Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1?max_rows_to_read=1");
        Statement statement = connection.createStatement();
        statement.execute("SELECT 1 UNION ALL SELECT 2");
    }

    @Test
    public void successfullyUrlParser() throws Exception {
        Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
        String url = "jdbc:clickhouse://127.0.0.1/system?min_insert_block_size_rows=1000";
        ClickHouseConfig config = new ClickHouseConfig(url , new Properties());
        Assert.assertEquals(config.database(), "system");
        Assert.assertEquals(config.settings().get(SettingKey.min_insert_block_size_rows), 1000L);
    }
}
