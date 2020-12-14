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

import com.github.housepower.jdbc.misc.Validate;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickHouseConfig {

    public static final Pattern DB_PATH_PATTERN = Pattern.compile("/([a-zA-Z0-9_]+)");
    public static final Pattern HOST_PORT_PATH_PATTERN = Pattern.compile("//(?<host>[^/:\\s]+)(:(?<port>\\d+))?");

    private final int port;
    private final String address;
    private String database;
    private final String username;
    private final String password;
    private int soTimeout;
    private final int connectTimeout;
    private final Map<SettingKey, Object> settings;

    private ClickHouseConfig(int port, String address, String database, String username, String password,
                             int soTimeout, int connectTimeout, Map<SettingKey, Object> settings) {
        this.port = port;
        this.address = address;
        this.database = database;
        this.username = username;
        this.password = password;
        this.soTimeout = soTimeout;
        this.connectTimeout = connectTimeout;
        this.settings = settings;
    }

    public ClickHouseConfig(String url, Properties properties) throws SQLException {
        this.settings = parseJDBCUrl(url);
        this.settings.putAll(parseJDBCProperties(properties));

        Object obj;
        this.port = (obj = settings.remove(SettingKey.port)) == null ? 9000 : ((int) obj) == -1 ? 9000 : (int) obj;
        this.address = (obj = settings.remove(SettingKey.address)) == null ? "127.0.0.1" : String.valueOf(obj);
        this.password = (obj = settings.remove(SettingKey.password)) == null ? "" : String.valueOf(obj);
        this.username = (obj = settings.remove(SettingKey.user)) == null ? "default" : String.valueOf(obj);
        this.database = (obj = settings.remove(SettingKey.database)) == null ? "default" : String.valueOf(obj);

        // Java use time unit mills @
        // https://docs.oracle.com/javase/7/docs/api/java/net/Socket.html#connect(java.net.SocketAddress,%20int)
        this.soTimeout = (obj = settings.remove(SettingKey.query_timeout)) == null ? 0 : (int) obj * 1000;
        this.connectTimeout = (obj = settings.remove(SettingKey.connect_timeout)) == null ? 0 : (int) obj * 1000;
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

    public String database(String database) {
        return this.database = database;
    }

    public String username() {
        return this.username;
    }

    public String password() {
        return this.password;
    }

    public int queryTimeout() {
        return this.soTimeout;
    }

    public int connectTimeout() {
        return this.connectTimeout;
    }

    public Map<SettingKey, Object> settings() {
        return settings;
    }

    public Map<SettingKey, Object> parseJDBCProperties(Properties properties) {
        Map<SettingKey, Object> settings = new HashMap<>();

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            for (SettingKey settingKey : SettingKey.values()) {
                String name = String.valueOf(entry.getKey());
                if (settingKey.name().equalsIgnoreCase(name)) {
                    settings.put(settingKey, settingKey.type().deserializeURL(String.valueOf(entry.getValue())));
                }
            }
        }

        return settings;
    }

    private Map<SettingKey, Object> parseJDBCUrl(String jdbcUrl) throws SQLException {
        try {
            URI uri = new URI(jdbcUrl.substring(5));

            String host = parseHost(jdbcUrl);
            Integer port = parsePort(jdbcUrl);
            String database = parseDatabase(jdbcUrl);
            Map<SettingKey, Object> settings = new HashMap<>();
            settings.put(SettingKey.address, host);
            settings.put(SettingKey.port, port);
            settings.put(SettingKey.database, database);
            settings.putAll(extractQueryParameters(uri.getQuery()));

            return settings;
        } catch (URISyntaxException ex) {
            throw new SQLException(ex.getMessage(), ex);
        }
    }

    private String parseDatabase(String jdbcUrl) throws URISyntaxException {
        URI uri = new URI(jdbcUrl.substring(5));
        String database = uri.getPath();
        if (database != null && !database.isEmpty()) {
            Matcher m = DB_PATH_PATTERN.matcher(database);
            if (m.matches()) {
                database = m.group(1);
            } else {
                throw new URISyntaxException("wrong database name path: '" + database + "'", jdbcUrl);
            }
        }
        if (database != null && database.isEmpty()) {
            database = "default";
        }
        return database;
    }

    private String parseHost(String jdbcUrl) throws URISyntaxException {
        String uriStr = jdbcUrl.substring(5);
        URI uri = new URI(uriStr);
        String host = uri.getHost();
        if (host == null || host.isEmpty()) {
            Matcher m = HOST_PORT_PATH_PATTERN.matcher(uriStr);
            if (m.find()) {
                host = m.group("host");
            } else {
                throw new URISyntaxException("No valid host was found", jdbcUrl);
            }
        }
        return host;
    }

    private int parsePort(String jdbcUrl) throws URISyntaxException {
        String uriStr = jdbcUrl.substring(5);
        URI uri = new URI(uriStr);
        int port = uri.getPort();
        if (port <= -1) {
            Matcher m = HOST_PORT_PATH_PATTERN.matcher(uriStr);
            if (m.find() && m.group("port") != null) {
                port = Integer.parseInt(m.group("port"));
            }
        }
        return port;
    }

    private Map<SettingKey, Object> extractQueryParameters(String queryParameters) throws SQLException {
        Map<SettingKey, Object> parameters = new HashMap<>();
        StringTokenizer tokenizer = new StringTokenizer(queryParameters == null ? "" : queryParameters, "&");

        while (tokenizer.hasMoreTokens()) {
            String[] queryParameter = tokenizer.nextToken().split("=", 2);
            Validate.isTrue(queryParameter.length == 2,
                    "ClickHouse JDBC URL Parameter '" + queryParameters + "' Error, Expected '='.");

            for (SettingKey settingKey : SettingKey.values()) {
                if (settingKey.name().equalsIgnoreCase(queryParameter[0])) {
                    parameters.put(settingKey, settingKey.type().deserializeURL(queryParameter[1]));
                }
            }
        }
        return parameters;
    }

    public void setQueryTimeout(int timeout) {
        this.soTimeout = timeout;
    }

    public ClickHouseConfig copy() {
        return new ClickHouseConfig(port, address, database, username, password,
                soTimeout, connectTimeout, new HashMap<>(this.settings));
    }
}
