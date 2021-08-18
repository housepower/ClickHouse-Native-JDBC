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

package com.github.housepower.settings;

import com.github.housepower.serde.SettingType;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ClickHouseConfigTest {

    @Test
    public void testDefaultClickHouseConfig() {
        ClickHouseConfig cfg = ClickHouseConfig.Builder.builder().build();
        assertEquals("127.0.0.1", cfg.host());
        assertEquals(9000, cfg.port());
        assertEquals("default", cfg.user());
        assertEquals("", cfg.password());
        assertEquals(Duration.ZERO, cfg.queryTimeout());
        assertEquals(Duration.ZERO, cfg.connectTimeout());
        assertEquals(StandardCharsets.UTF_8, cfg.charset());
        assertFalse(cfg.tcpKeepAlive());
        assertEquals("default", cfg.database());
        assertEquals("jdbc:clickhouse://127.0.0.1:9000/default?query_timeout=0&connect_timeout=0&charset=UTF-8&client_name=ClickHouse client&tcp_keep_alive=false",
                cfg.jdbcUrl());
    }

    @Test
    public void testClickHouseConfig() {
        ClickHouseConfig cfg = ClickHouseConfig.Builder.builder()
                .withJdbcUrl("jdbc:clickhouse://1.2.3.4:8123/db2")
                .charset("GBK")
                .clientName("ck-test")
                .withSetting(SettingKey.allow_distributed_ddl, true)
                .build()
                .withCredentials("user", "passWorD");
        assertEquals("1.2.3.4", cfg.host());
        assertEquals(8123, cfg.port());
        assertEquals("user", cfg.user());
        assertEquals("passWorD", cfg.password());
        assertEquals(Duration.ZERO, cfg.queryTimeout());
        assertEquals(Duration.ZERO, cfg.connectTimeout());
        assertEquals(Charset.forName("GBK"), cfg.charset());
        assertFalse(cfg.tcpKeepAlive());
        assertEquals("db2", cfg.database());
        assertEquals("jdbc:clickhouse://1.2.3.4:8123/db2?query_timeout=0&connect_timeout=0&charset=GBK&client_name=ck-test&tcp_keep_alive=false&allow_distributed_ddl=true",
                cfg.jdbcUrl());
    }

    @Test
    public void testUndefinedSettings() {
        Properties props = new Properties();
        props.setProperty("unknown", "unknown");
        ClickHouseConfig cfg = ClickHouseConfig.Builder.builder()
                .withProperties(props)
                .build();
        assertTrue(cfg.settings()
                .keySet()
                .stream()
                .noneMatch(settingKey -> settingKey.name().equalsIgnoreCase("unknown")));
    }

    @Test
    public void testUserDefinedSettings() {
        SettingKey userDefined = SettingKey.builder()
                .withName("user_defined")
                .withType(SettingType.UTF8)
                .build();

        Properties props = new Properties();
        props.setProperty("user_defined", "haha");

        ClickHouseConfig cfg = ClickHouseConfig.Builder.builder()
                .withProperties(props)
                .build();
        assertEquals("haha", cfg.settings().get(userDefined));
    }
}
