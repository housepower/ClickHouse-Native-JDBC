package com.github.housepower.jdbc;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Properties;

public class ClickhouseDriverRegisterTest {

    private static final int SERVER_PORT = Integer.valueOf(System.getProperty("CLICK_HOUSE_SERVER_PORT", "9000"));

    @Test
    public void successfullyCreateConnection() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("user", "user");
        properties.setProperty("password", "password");
        String mockedUrl = "jdbc:mock://127.0.0.1:" + SERVER_PORT;
        Driver mockedDriver = Mockito.mock(Driver.class);
        Connection mockedConnection = Mockito.mock(Connection.class);
        Mockito.when(mockedDriver.acceptsURL(mockedUrl)).thenReturn(true);
        Mockito.when(mockedDriver.connect(mockedUrl, properties)).thenReturn(mockedConnection);

        Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
        DriverManager.registerDriver(mockedDriver);
        Assert.assertEquals(mockedConnection, DriverManager.getConnection(mockedUrl, properties));
    }

}
