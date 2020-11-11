package com.github.housepower.jdbc;

import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.settings.SettingKey;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickhouseJdbcUrlParser {
    public static final String JDBC_PREFIX = "jdbc:";
    public static final String CLICKHOUSE_PREFIX = "clickhouse:";
    public static final String JDBC_CLICKHOUSE_PREFIX = JDBC_PREFIX + CLICKHOUSE_PREFIX;

    public static final Pattern DB_PATH_PATTERN = Pattern.compile("/([a-zA-Z0-9_]+)");
    public static final Pattern HOST_PORT_PATH_PATTERN = Pattern.compile("//(?<host>[^/:\\s]+)(:(?<port>\\d+))?");

    private static final Logger LOG = LogManager.getLogManager().getLogger(ClickhouseJdbcUrlParser.class.getName());

    public static Map<SettingKey, Object> parseProperties(Properties properties) {
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

    public static Map<SettingKey, Object> parseJdbcUrl(String jdbcUrl) throws SQLException {
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

    private static String parseDatabase(String jdbcUrl) throws URISyntaxException {
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

    private static String parseHost(String jdbcUrl) throws URISyntaxException {
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

    private static int parsePort(String jdbcUrl) throws URISyntaxException {
        String uriStr = jdbcUrl.substring(5);
        URI uri = new URI(uriStr);
        int port = uri.getPort();
        if (port <= -1) {
            Matcher m = HOST_PORT_PATH_PATTERN.matcher(uriStr);
            if (m.find() && m.group("port") != null) {
                port = Integer.parseInt(m.group("port"));
            }
        }
        if (port == 8123) {
            LOG.log(Level.WARNING, "8123 is default HTTP port, you may connect with error protocol!");
        }
        return port;
    }

    public static Map<SettingKey, Object> extractQueryParameters(String queryParameters) throws SQLException {
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
}
