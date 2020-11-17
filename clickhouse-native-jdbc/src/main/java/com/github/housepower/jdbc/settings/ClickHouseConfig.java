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

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ClickHouseConfig {

    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;
    private final int queryTimeoutMs;
    private final int connectTimeoutMs;
    private final Charset charset;
    private final Map<SettingKey, Object> settings;

    private ClickHouseConfig(int port, String host, String database, String user, String password,
                             int queryTimeoutMs, int connectTimeoutMs, Charset charset, Map<SettingKey, Object> settings) {
        this.port = port;
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;
        this.queryTimeoutMs = queryTimeoutMs;
        this.connectTimeoutMs = connectTimeoutMs;
        this.charset = charset;
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

    public String user() {
        return this.user;
    }

    public String password() {
        return this.password;
    }

    public int queryTimeout() {
        return this.queryTimeoutMs;
    }

    public int connectTimeout() {
        return this.connectTimeoutMs;
    }

    public Charset charset() {
        return this.charset;
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

    public ClickHouseConfig withDatabase(String database) {
        return Builder.builder(this)
                .database(database)
                .build();
    }

    public ClickHouseConfig withCredentials(String user, String password) {
        return Builder.builder(this)
                .user(user)
                .password(password)
                .build();
    }

    public ClickHouseConfig withQueryTimeout(Duration timeout) {
        return Builder.builder(this)
                .queryTimeoutMs((int) timeout.toMillis())
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
        private String host;
        private int port;
        private String database;
        private String user;
        private String password;
        private int connectTimeoutMs;
        private int queryTimeoutMs;
        private Charset charset;
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
                    .user(cfg.user())
                    .password(cfg.password())
                    .connectTimeoutMs(cfg.connectTimeout())
                    .queryTimeoutMs(cfg.queryTimeout())
                    .charset(cfg.charset().name())
                    .withSettings(cfg.settings());
        }

        private Builder charset(String charset) {
            this.withSetting(SettingKey.characterEncoding, charset);
            return this;
        }

        public Builder withSetting(SettingKey key, Object value) {
            this.settings.put(key, value);
            return this;
        }

        public Builder withSettings(Map<SettingKey, Object> settings) {
            CollectionUtil.mergeMapInPlaceKeepLast(this.settings, settings);
            return this;
        }

        public Builder host(String host) {
            this.withSetting(SettingKey.host, host);
            return this;
        }

        public Builder port(int port) {
            this.withSetting(SettingKey.port, port);
            return this;
        }

        public Builder database(String database) {
            this.withSetting(SettingKey.database, database);
            return this;
        }

        public Builder user(String user) {
            this.withSetting(SettingKey.user, user);
            return this;
        }

        public Builder password(String password) {
            this.withSetting(SettingKey.password, password);
            return this;
        }

        public Builder connectTimeoutMs(int connectTimeoutMs) {
            this.withSetting(SettingKey.connect_timeout, connectTimeoutMs);
            return this;
        }

        public Builder queryTimeoutMs(int queryTimeoutMs) {
            this.withSetting(SettingKey.query_timeout, queryTimeoutMs);
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

        public Builder withJdbcUrl(String jdbcUrl) {
            return this.withSettings(ClickhouseJdbcUrlParser.parseJdbcUrl(jdbcUrl));
        }

        public Builder withProperties(Properties properties) {
            return this.withSettings(ClickhouseJdbcUrlParser.parseProperties(properties));
        }

        public ClickHouseConfig build() {
            this.host = (String) this.settings.getOrDefault(SettingKey.host, "127.0.0.1");
            this.port = (int) this.settings.getOrDefault(SettingKey.port, 9000);
            this.user = (String) this.settings.getOrDefault(SettingKey.user, "default");
            this.password = (String) this.settings.getOrDefault(SettingKey.password, "");
            this.database = (String) this.settings.getOrDefault(SettingKey.database, "default");
            this.connectTimeoutMs = (int) this.settings.getOrDefault(SettingKey.connect_timeout, 0) * 1000;
            this.queryTimeoutMs = (int) this.settings.getOrDefault(SettingKey.query_timeout, 0) * 1000;
            String characterEncoding = (String) this.settings.getOrDefault(SettingKey.characterEncoding, "UTF-8");
            charset = Charset.forName(characterEncoding);

            revisit();
            purgeSettings();

            return new ClickHouseConfig(
                    port, host, database, user, password, queryTimeoutMs, connectTimeoutMs, charset, settings);
        }

        private void revisit() {
            if (this.port == -1) this.port = 9000;
            if (StrUtil.isBlank(this.host)) this.host = "127.0.0.1";
            if (StrUtil.isBlank(this.user)) this.user = "default";
            if (StrUtil.isBlank(this.password)) this.password = "";
            if (StrUtil.isBlank(this.database)) this.database = "default";
            if (this.queryTimeoutMs < 0) this.queryTimeoutMs = 0;
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
            this.settings.remove(SettingKey.characterEncoding);
        }
    }
}
