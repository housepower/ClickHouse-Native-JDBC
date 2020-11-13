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
import com.github.housepower.jdbc.misc.CollectionUtil;
import com.github.housepower.jdbc.misc.StrUtil;

import javax.annotation.concurrent.Immutable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Immutable
public class ClickHouseConfig {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final int soTimeoutMs;
    private final int connectTimeoutMs;
    private final Map<SettingKey, Object> settings;

    private ClickHouseConfig(int port, String host, String database, String username, String password,
                             int soTimeoutMs, int connectTimeoutMs, Map<SettingKey, Object> settings) {
        this.port = port;
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.soTimeoutMs = soTimeoutMs;
        this.connectTimeoutMs = connectTimeoutMs;
        this.settings = settings;
    }

    public int port() {
        return this.port;
    }

    public String host() {
        return this.host;
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

    public ClickHouseConfig withHostPort(String host, int port) {
        return Builder.builder(this)
                .host(host)
                .port(port)
                .build();
    }

    public ClickHouseConfig withCredentials(String username, String password) {
        return Builder.builder(this)
                .username(username)
                .password(password)
                .build();
    }

    public ClickHouseConfig withQueryTimeout(Duration timeout) {
        return Builder.builder(this)
                .soTimeoutMs((int) timeout.toMillis())
                .build();
    }

    public ClickHouseConfig withSettings(Map<SettingKey, Object> settings) {
        return Builder.builder(this)
                .withSettings(settings)
                .build();
    }

    public ClickHouseConfig withJdbcUrl(String url) {
        return Builder.builder(this)
                .withJdbcUrl(url)
                .build();
    }

    public ClickHouseConfig withProperties(Properties properties) {
        return Builder.builder(this)
                .withProperties(properties)
                .build();
    }

    public ClickHouseConfig with(String url, Properties properties) {
        return Builder.builder(this)
                .withJdbcUrl(url)
                .withProperties(properties)
                .build();
    }

    public static final class Builder {
        private String host = "127.0.0.1";
        private int port = 9000;
        private String database = "default";
        private String username = "default";
        private String password = "";
        private int soTimeoutMs = 0;
        private int connectTimeoutMs = 0;
        private Map<SettingKey, Object> settings = new HashMap<>();

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public static Builder builder(ClickHouseConfig cfg) {
            return new Builder()
                    .host(cfg.host())
                    .port(cfg.port())
                    .database(cfg.database())
                    .username(cfg.username())
                    .password(cfg.password())
                    .soTimeoutMs(cfg.queryTimeout())
                    .connectTimeoutMs(cfg.connectTimeout())
                    .settings(new HashMap<>(cfg.settings()));
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder database(String database) {
            this.database = database;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder soTimeoutMs(int soTimeoutMs) {
            this.soTimeoutMs = soTimeoutMs;
            return this;
        }

        public Builder connectTimeoutMs(int connectTimeoutMs) {
            this.connectTimeoutMs = connectTimeoutMs;
            return this;
        }

        public Builder settings(Map<SettingKey, Object> settings) {
            this.settings = settings;
            return this;
        }

        public Builder clearSettings() {
            this.settings = new HashMap<>();
            return this;
        }

        public Builder withSettings(Map<SettingKey, Object> settings) {
            CollectionUtil.mergeMapInPlaceKeepLast(this.settings, settings);
            return this;
        }

        public Builder withJdbcUrl(String jdbcUrl) {
            return this.withSettings(ClickhouseJdbcUrlParser.parseJdbcUrl(jdbcUrl));
        }

        public Builder withProperties(Properties properties) {
            return this.withSettings(ClickhouseJdbcUrlParser.parseProperties(properties));
        }

        public ClickHouseConfig build() {
            this.port = (int) this.settings.getOrDefault(SettingKey.port, 9000);
            this.host = (String) this.settings.getOrDefault(SettingKey.host, "127.0.0.1");
            this.password = (String) this.settings.getOrDefault(SettingKey.password, "");
            this.username = (String) this.settings.getOrDefault(SettingKey.user, "default");
            this.database = (String) this.settings.getOrDefault(SettingKey.database, "default");
            this.soTimeoutMs = (int) this.settings.getOrDefault(SettingKey.query_timeout, 0) * 1000;
            this.connectTimeoutMs = (int) this.settings.getOrDefault(SettingKey.connect_timeout, 0) * 1000;

            revisit();
            purgeSettings();

            return new ClickHouseConfig(
                    port, host, database, username, password, soTimeoutMs, connectTimeoutMs, settings);
        }

        private void revisit() {
            if (this.port == -1) this.port = 9000;
            if (StrUtil.isBlank(this.host)) this.host = "127.0.0.1";
            if (StrUtil.isBlank(this.username)) this.username = "default";
            if (StrUtil.isBlank(this.password)) this.password = "";
            if (StrUtil.isBlank(this.database)) this.database = "default";
            if (this.soTimeoutMs < 0) this.soTimeoutMs = 0;
            if (this.connectTimeoutMs < 0) this.connectTimeoutMs = 0;
        }

        private void purgeSettings() {
            this.settings.remove(SettingKey.port);
            this.settings.remove(SettingKey.host);
            this.settings.remove(SettingKey.password);
            this.settings.remove(SettingKey.user);
            this.settings.remove(SettingKey.database);
            this.settings.remove(SettingKey.query_timeout);
            this.settings.remove(SettingKey.connect_timeout);
        }
    }
}
