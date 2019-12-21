package com.github.housepower.jdbc;

import com.github.housepower.jdbc.tool.EmbeddedDriver;
import org.junit.Assert;
import org.junit.Test;

import java.sql.DriverManager;
import java.util.Properties;

public class ClickhouseDriverRegisterTest {

    private static final int SERVER_PORT = Integer.valueOf(System.getProperty("CLICK_HOUSE_SERVER_PORT", "9000"));

    @Test
    public void successfullyCreateConnection() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("user", "user");
        properties.setProperty("password", "password");
        String mockedUrl = EmbeddedDriver.EMBEDDED_DRIVER_PREFIX + "//127.0.0.1:" + SERVER_PORT;

        DriverManager.registerDriver(new EmbeddedDriver());
        Assert.assertEquals(EmbeddedDriver.MOCKED_CONNECTION, DriverManager.getConnection(mockedUrl, properties));
    }

}
