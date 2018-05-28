package org.houseflys.jdbc.settings;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.StringTokenizer;

public class ClickHouseURL {

    private static final String JDBC_PREFIX = "jdbc:";
    private static final String JDBC_CLICKHOUSE_PREFIX = JDBC_PREFIX + "clickhouse:";
    private static final Properties EMPTY_PROPERTIES = new Properties();

    private final String url;
    private final Properties properties;

    public ClickHouseURL(String url) {
        this(url, EMPTY_PROPERTIES);
    }

    public ClickHouseURL(String url, Properties properties) {
        this.url = url;
        this.properties = properties;
    }

    public boolean accept() {
        return url.startsWith(JDBC_CLICKHOUSE_PREFIX);
    }

    public ClickHouseConfig asConfig() throws SQLException {
        try {
            if (!accept()) {
                throw new SQLException(url, "Expected '" + JDBC_CLICKHOUSE_PREFIX + "' prefix.");
            }

            URI uri = new URI(url.substring(JDBC_PREFIX.length()));

            Settings settings = Settings.fromProperties(queryPartAsProperties(uri.getQuery()));
            return new ClickHouseConfig(uri.getHost(), uri.getPort(), uri.getPath(), settings);
        } catch (URISyntaxException ex) {
            throw new SQLException(ex.getMessage(), ex);
        }
    }

    private Properties queryPartAsProperties(String query) throws SQLException {
        Properties queryProperties = new Properties(properties);

        if (query != null) {
            StringTokenizer queryTokenizer = new StringTokenizer(query, "&");

            while (queryTokenizer.hasMoreTokens()) {
                String argument = queryTokenizer.nextToken();
                String[] argumentNameAndVal = argument.split("=", 2);

                if (argumentNameAndVal.length != 2) {
                    throw new SQLException(argument, "Expected '='");
                }

                queryProperties.setProperty(argumentNameAndVal[0], argumentNameAndVal[1]);
            }
        }
        
        return queryProperties;
    }
}
