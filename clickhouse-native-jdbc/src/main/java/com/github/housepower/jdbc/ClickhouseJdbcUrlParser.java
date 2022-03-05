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

import com.github.housepower.exception.InvalidValueException;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.misc.Validate;
import com.github.housepower.settings.SettingKey;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickhouseJdbcUrlParser {
    public static final String JDBC_PREFIX = "jdbc:";
    public static final String CLICKHOUSE_PREFIX = "clickhouse:";
    public static final String JDBC_CLICKHOUSE_PREFIX = JDBC_PREFIX + CLICKHOUSE_PREFIX;

    public static final String HOST_DELIMITER = ",";
    public static final String PORT_DELIMITER = ":";

    /**
     * Jdbc Url sames like:
     * '//[host1][:port1],[host2][:port2],[host3][:port3]]...[/[database]][?propertyName1=propertyValue1[&propertyName2=propertyValue2]...]'
     *
     * Default_port is used when port does not exist.
     */
    public static final Pattern CONNECTION_PATTERN = Pattern.compile("//(?<hosts>([^/?:,\\s]+(:\\d+)?)(,[^/?:,\\s]+(:\\d+)?)*)" // hosts: required; starts with "//" followed by any char except "/", "?"
            + "(?:/(?<database>([a-zA-Z0-9_]+)))?" // database: optional; starts with "/", and then followed by any char except "?"
            + "(?:\\?(?<properties>.*))?"); // properties: optional; starts with "?", and then followed by any char

    private static final Logger LOG = LoggerFactory.getLogger(ClickhouseJdbcUrlParser.class);

    public static Map<SettingKey, Serializable> parseJdbcUrl(String jdbcUrl) {
        String uri = jdbcUrl.substring(JDBC_CLICKHOUSE_PREFIX.length());
        Matcher matcher = CONNECTION_PATTERN.matcher(uri);
        if (!matcher.matches()) {
            throw new InvalidValueException("Connection is not support");
        }

        Map<SettingKey, Serializable> settings = new HashMap<>();

        String hosts = matcher.group("hosts");
        String database = matcher.group("database");
        String properties = matcher.group("properties");

        if (hosts.contains(HOST_DELIMITER)) { // multi-host
            settings.put(SettingKey.host, hosts);
        } else { // standard-host
            String[] hostAndPort = hosts.split(PORT_DELIMITER, 2);

            settings.put(SettingKey.host, hostAndPort[0]);

            if (hostAndPort.length == 2) {
                if (Integer.parseInt(hostAndPort[1]) == 8123) {
                    LOG.warn("8123 is default HTTP port, you may connect with error protocol!");
                }
                settings.put(SettingKey.port, Integer.parseInt(hostAndPort[1]));
            }
        }

        settings.put(SettingKey.database, database);
        settings.putAll(extractQueryParameters(properties));

        return settings;
    }

    public static Map<SettingKey, Serializable> parseProperties(Properties properties) {
        Map<SettingKey, Serializable> settings = new HashMap<>();

        for (String name : properties.stringPropertyNames()) {
            String value = properties.getProperty(name);

            parseSetting(settings, name, value);
        }

        return settings;
    }

    public static Map<SettingKey, Serializable> extractQueryParameters(String queryParameters) {
        Map<SettingKey, Serializable> parameters = new HashMap<>();
        StringTokenizer tokenizer = new StringTokenizer(queryParameters == null ? "" : queryParameters, "&");

        while (tokenizer.hasMoreTokens()) {
            String[] queryParameter = tokenizer.nextToken().split("=", 2);
            Validate.ensure(queryParameter.length == 2,
                    "ClickHouse JDBC URL Parameter '" + queryParameters + "' Error, Expected '='.");

            String name = queryParameter[0];
            String value = queryParameter[1];

            parseSetting(parameters, name, value);
        }
        return parameters;
    }

    private static void parseSetting(Map<SettingKey, Serializable> settings, String name, String value) {
        SettingKey settingKey = SettingKey.definedSettingKeys().get(name.toLowerCase(Locale.ROOT));
        if (settingKey != null) {
            settings.put(settingKey, settingKey.type().deserializeURL(value));
        } else {
            LOG.warn("ignore undefined setting: {}={}", name, value);
        }
    }
}
