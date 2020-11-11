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

package com.github.housepower.jdbc.settings;

import com.github.housepower.jdbc.ClickhouseJdbcUrlParser;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ClickHouseConfig {

    private final int port;
    private final String address;
    private final String database;
    private final String username;
    private final String password;
    private int soTimeoutMs;
    private final int connectTimeoutMs;
    private final Map<SettingKey, Object> settings;

    public ClickHouseConfig(String url, Properties properties) throws SQLException {
        if (url.isEmpty())
            this.settings = new HashMap<>();
        else
            this.settings = ClickhouseJdbcUrlParser.parseJDBCUrl(url);
        this.settings.putAll(ClickhouseJdbcUrlParser.parseJDBCProperties(properties));

        int _port = (int) settings.getOrDefault(SettingKey.port, 9000);
        this.port = _port == -1 ? 9000 : _port;
        this.address = (String) settings.getOrDefault(SettingKey.address, "127.0.0.1");
        this.password = (String) settings.getOrDefault(SettingKey.password, "");
        this.username = (String) settings.getOrDefault(SettingKey.user, "default");
        this.database = (String) settings.getOrDefault(SettingKey.database, "default");
        this.soTimeoutMs = (int) settings.getOrDefault(SettingKey.query_timeout, 0) * 1000;
        this.connectTimeoutMs = (int) settings.getOrDefault(SettingKey.connect_timeout, 0) * 1000;

        // the rest will send to server in each block
        settings.remove(SettingKey.port);
        settings.remove(SettingKey.address);
        settings.remove(SettingKey.password);
        settings.remove(SettingKey.user);
        settings.remove(SettingKey.database);
        settings.remove(SettingKey.query_timeout);
        settings.remove(SettingKey.connect_timeout);
    }

    private ClickHouseConfig(int port, String address, String database, String username, String password,
                             int soTimeoutMs, int connectTimeoutMs, Map<SettingKey, Object> settings) {
        this.port = port;
        this.address = address;
        this.database = database;
        this.username = username;
        this.password = password;
        this.soTimeoutMs = soTimeoutMs;
        this.connectTimeoutMs = connectTimeoutMs;
        this.settings = settings;
    }

    public ClickHouseConfig copy() {
        return new ClickHouseConfig(port, address, database, username, password,
                soTimeoutMs, connectTimeoutMs, new HashMap<>(this.settings));
    }

    public int port() {
        return this.port;
    }

    public String address() {
        return this.address;
    }

    public String database() {
        return this.database;
    }

    public String username() {
        return this.username;
    }

    public String password() {
        return this.password;
    }

    public int queryTimeout() {
        return this.soTimeoutMs;
    }

    public int connectTimeout() {
        return this.connectTimeoutMs;
    }

    public Map<SettingKey, Object> settings() {
        return settings;
    }

    public void setQueryTimeout(int timeout) {
        this.soTimeoutMs = timeout;
    }
}
