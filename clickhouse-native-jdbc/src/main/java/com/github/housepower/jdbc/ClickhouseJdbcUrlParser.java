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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.housepower.misc.StrUtil.isBlank;
import static com.github.housepower.misc.StrUtil.isNotBlank;

public class ClickhouseJdbcUrlParser {
    public static final String JDBC_PREFIX = "jdbc:";
    public static final String CLICKHOUSE_PREFIX = "clickhouse:";
    public static final String JDBC_CLICKHOUSE_PREFIX = JDBC_PREFIX + CLICKHOUSE_PREFIX;

    public static final String HOST_DELIMITER = ",";

    public static final Pattern DB_PATH_PATTERN = Pattern.compile("/([a-zA-Z0-9_]+)");
    public static final Pattern HOST_PORT_PATH_PATTERN = Pattern.compile("//(?<host>[^/:\\s]+)(:(?<port>\\d+))?");

    private static final Pattern CONNECTION_STRING_PTRN = Pattern.compile("(?<scheme>[\\w\\+:%]+)\\s*" // scheme: required; alphanumeric, plus, colon or percent
            + "(?://(?<hosts>[^/?#]*))?\\s*" // authority: optional; starts with "//" followed by any char except "/", "?" and "#"
            + "(?:/(?!\\s*/)(?<database>[^?#]*))?" // path: optional; starts with "/" but not followed by "/", and then followed by by any char except "?" and "#"
            + "(?:\\?(?!\\s*\\?)(?<queryParameters>[^#]*))?" // query: optional; starts with "?" but not followed by "?", and then followed by by any char except "#"
            + "(?:\\s*#(?<fragment>.*))?"); // fragment: optional; starts with "#", and then followed by anything

    private static final Logger LOG = LoggerFactory.getLogger(ClickhouseJdbcUrlParser.class);

    private final String baseConnectionString;
    private String scheme;
    private String hosts;
    private String database;
    private String queryParameters;

    public ClickhouseJdbcUrlParser(String jdbcUrl) {
        this.baseConnectionString = jdbcUrl;
        parseConnectionString();
    }

    /**
     * Static factory method for constructing instances of this class.
     *
     * @param connString The connection string to parse.
     * @return an instance of {@link ClickhouseJdbcUrlParser}
     */
    public static ClickhouseJdbcUrlParser parseConnectionString(String connString) {
        return new ClickhouseJdbcUrlParser(connString);
    }


    public static Map<SettingKey, Serializable> parseJdbcUrl(String jdbcUrl) {
        try {
            URI uri = new URI(jdbcUrl.substring(JDBC_PREFIX.length()));
            String host = parseHost(jdbcUrl);
            Integer port = parsePort(jdbcUrl);
            String database = parseDatabase(jdbcUrl);
            Map<SettingKey, Serializable> settings = new HashMap<>();
            settings.put(SettingKey.host, host);
            settings.put(SettingKey.port, port);
            settings.put(SettingKey.database, database);
            settings.putAll(extractQueryParameters(uri.getQuery()));

            return settings;
        } catch (URISyntaxException ex) {
            throw new InvalidValueException(ex);
        }
    }

    public static Map<SettingKey, Serializable> parseProperties(Properties properties) {
        Map<SettingKey, Serializable> settings = new HashMap<>();

        for (String name : properties.stringPropertyNames()) {
            String value = properties.getProperty(name);

            parseSetting(settings, name, value);
        }

        return settings;
    }

    private static String parseDatabase(String jdbcUrl) throws URISyntaxException {
        URI uri = new URI(jdbcUrl.substring(JDBC_PREFIX.length()));
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

    private static String parseHost(String jdbcUrl) throws URISyntaxException {
        String uriStr = jdbcUrl.substring(JDBC_PREFIX.length());
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

    private static int parsePort(String jdbcUrl) {
        String uriStr = jdbcUrl.substring(JDBC_PREFIX.length());
        URI uri;
        try {
            uri = new URI(uriStr);
        } catch (Exception ex) {
            throw new InvalidValueException(ex);
        }
        int port = uri.getPort();
        if (port <= -1) {
            Matcher m = HOST_PORT_PATH_PATTERN.matcher(uriStr);
            if (m.find() && m.group("port") != null) {
                port = Integer.parseInt(m.group("port"));
            }
        }
        if (port == 8123) {
            LOG.warn("8123 is default HTTP port, you may connect with error protocol!");
        }
        return port;
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

    public List<String> getSingleHostJdbcUrls() {
        return Arrays.stream(hosts.split(HOST_DELIMITER))
                .map(host -> {
                    String url = scheme + "//" + host;

                    if (isNotBlank(database)) {
                        url += "/" + database;
                    }

                    if (isNotBlank(queryParameters)) {
                        url += "?" + queryParameters;
                    }

                    return url;
                }).collect(Collectors.toList());
    }

    private static void parseSetting(Map<SettingKey, Serializable> settings, String name, String value) {
        SettingKey settingKey = SettingKey.definedSettingKeys().get(name.toLowerCase(Locale.ROOT));
        if (settingKey != null) {
            settings.put(settingKey, settingKey.type().deserializeURL(value));
        } else {
            LOG.warn("ignore undefined setting: {}={}", name, value);
        }
    }

    /**
     * Splits the connection string in its main sections.
     */
    private void parseConnectionString() {
        Matcher matcher = CONNECTION_STRING_PTRN.matcher(this.baseConnectionString);
        if (!matcher.matches()) {
            throw new InvalidValueException("Connection is not support ");
        }
        this.scheme = decodeSkippingPlusSign(matcher.group("scheme"));
        this.hosts = matcher.group("hosts"); // Don't decode just yet.
        this.database = matcher.group("database") == null ? null : decode(matcher.group("database")).trim();
        this.queryParameters = matcher.group("queryParameters"); // Don't decode just yet.
    }

    /**
     * URL-decode the given string skipping all occurrences of the plus sign.
     *
     * @param text the string to decode
     * @return the decoded string
     */
    private static String decodeSkippingPlusSign(String text) {
        if (isBlank(text)) {
            return text;
        }
        text = text.replace("+", "%2B"); // Percent encode for "+" is "%2B".
        try {
            return URLDecoder.decode(text, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // Won't happen.
        }
        return "";
    }

    /**
     * URL-decode the given string.
     *
     * @param text the string to decode
     * @return the decoded string
     */
    private static String decode(String text) {
        if (isBlank(text)) {
            return text;
        }
        try {
            return URLDecoder.decode(text, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // Won't happen.
        }
        return "";
    }

    public List<String> getHosts() {
        return Arrays.asList(this.hosts.split(HOST_DELIMITER));
    }

    public String getScheme() {
        return this.scheme;
    }
}
