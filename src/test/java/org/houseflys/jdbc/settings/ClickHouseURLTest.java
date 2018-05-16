package org.houseflys.jdbc.settings;

import org.junit.Assert;
import org.junit.Test;

public class ClickHouseURLTest {

    @Test
    public void onlyHostAndPort() throws Exception {
        ClickHouseURL url = new ClickHouseURL("jdbc:clickhouse://test_host:123");

        Assert.assertTrue(url.accept());
    }
}
