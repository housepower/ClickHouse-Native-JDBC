package com.github.housepower.jdbc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ClickHouseConnectionITest extends AbstractITest {

    @Test
    public void testCatalog() throws Exception {
        withNewConnection(connection -> {
            assertEquals("default", connection.getCatalog());
            connection.setCatalog("abc");
            assertEquals("abc", connection.getCatalog());
        });
    }

    @Test
    public void testSchema() throws Exception {
        withNewConnection(connection -> {
            assertNull(connection.getSchema());
            connection.setCatalog("abc");
            assertNull(connection.getSchema());
        });
    }
}
