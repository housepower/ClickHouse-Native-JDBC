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

import com.github.housepower.jdbc.tool.EmbeddedDriver;
import com.github.housepower.misc.SystemUtil;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClickhouseDriverRegisterTest {

    private static final int SERVER_PORT = Integer.parseInt(SystemUtil.loadProp("CLICK_HOUSE_SERVER_PORT", "9000"));

    @Test
    public void successfullyCreateConnection() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("user", "user");
        properties.setProperty("password", "password");
        String mockedUrl = EmbeddedDriver.EMBEDDED_DRIVER_PREFIX + "//127.0.0.1:" + SERVER_PORT;

        DriverManager.registerDriver(new EmbeddedDriver());
        assertEquals(EmbeddedDriver.MOCKED_CONNECTION, DriverManager.getConnection(mockedUrl, properties));
    }

}
