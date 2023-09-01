/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.housepower.jdbc;

import com.github.housepower.jdbc.tool.LocalKeyStoreConfig;
import com.github.housepower.settings.KeyStoreConfig;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

public class ClickHouseConnectionITest extends AbstractITest {

    @Test
    void ping() throws Exception {
        withNewConnection(connection -> {
            withStatement(connection, stmt -> {
                ResultSet resultSet = stmt.executeQuery("SELECT 1");
                assertTrue(resultSet.next());
            });
        });
    }

    @Test
    void pingWithSecureConnection() throws Exception {
        withNewConnection(connection -> {
                    withStatement(connection, stmt -> {
                        ResultSet resultSet = stmt.executeQuery("SELECT 1");
                        assertTrue(resultSet.next());
                    });
                }, "ssl", "true",
                "ssl_mode", "disabled");
    }

    @Test
    void pingWithSecureConnectionAndVerification() throws Exception {
        KeyStoreConfig keyStoreConfig = new LocalKeyStoreConfig();

        withNewConnection(connection -> {
                    withStatement(connection, stmt -> {
                        ResultSet resultSet = stmt.executeQuery("SELECT 1");
                        assertTrue(resultSet.next());
                    });
                }, "ssl", "true",
                "ssl_mode", "verify_ca",
                "key_store_type", keyStoreConfig.getKeyStoreType(),
                "key_store_path", keyStoreConfig.getKeyStorePath(),
                "key_store_password", keyStoreConfig.getKeyStorePassword());
    }

    @Test
    public void testCatalog() throws Exception {
        withNewConnection(connection -> {
            assertNull(connection.getCatalog());
            connection.setCatalog("abc");
            assertNull(connection.getCatalog());
        });
    }

    @Test
    public void testSchema() throws Exception {
        withNewConnection(connection -> {
            assertEquals("default", connection.getSchema());
            connection.setSchema("abc");
            assertEquals("abc", connection.getSchema());
        });
    }
}
