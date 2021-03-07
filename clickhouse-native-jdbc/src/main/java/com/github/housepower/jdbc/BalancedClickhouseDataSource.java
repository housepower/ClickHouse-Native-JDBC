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
import com.github.housepower.misc.StrUtil;
import com.github.housepower.misc.Validate;
import com.github.housepower.settings.ClickHouseConfig;
import com.github.housepower.settings.SettingKey;
import com.github.housepower.jdbc.wrapper.SQLWrapper;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.sql.DataSource;

/**
 * <p> Database for clickhouse jdbc connections.
 * <p> It has list of database urls.
 * For every {@link #getConnection() getConnection} invocation, it returns connection to random host from the list.
 * Furthermore, this class has method { #scheduleActualization(int, TimeUnit) scheduleActualization}
 * which test hosts for availability. By default, this option is turned off.
 */
public final class BalancedClickhouseDataSource implements DataSource, SQLWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(BalancedClickhouseDataSource.class);
    private static final Pattern URL_TEMPLATE = Pattern.compile(ClickhouseJdbcUrlParser.JDBC_CLICKHOUSE_PREFIX +
            "//([a-zA-Z0-9_:,.-]+)" +
            "((/[a-zA-Z0-9_]+)?" +
            "([?][a-zA-Z0-9_]+[=][a-zA-Z0-9_]+([&][a-zA-Z0-9_]+[=][a-zA-Z0-9_]*)*)?" +
            ")?");

    private PrintWriter printWriter;
    private int loginTimeoutSeconds = 0;

    private final ThreadLocal<Random> randomThreadLocal = new ThreadLocal<>();
    private final List<String> allUrls;
    private volatile List<String> enabledUrls;

    private final ClickHouseConfig cfg;
    private final ClickHouseDriver driver = new ClickHouseDriver();

    /**
     * create Datasource for clickhouse JDBC connections
     *
     * @param url address for connection to the database, must have the next format
     *            {@code jdbc:clickhouse://<first-host>:<port>,<second-host>:<port>/<database>?param1=value1&param2=value2 }
     *            for example, {@code jdbc:clickhouse://localhost:9000,localhost:9000/database?compress=1&decompress=2 }
     * @throws IllegalArgumentException if param have not correct format,
     *                                  or error happens when checking host availability
     */
    public BalancedClickhouseDataSource(String url) {
        this(splitUrl(url), new Properties());
    }

    /**
     * create Datasource for clickhouse JDBC connections
     *
     * @param url        address for connection to the database
     * @param properties database properties
     * @see #BalancedClickhouseDataSource(String)
     */
    public BalancedClickhouseDataSource(String url, Properties properties) {
        this(splitUrl(url), properties);
    }

    /**
     * create Datasource for clickhouse JDBC connections
     *
     * @param url      address for connection to the database
     * @param settings clickhouse settings
     * @see #BalancedClickhouseDataSource(String)
     */
    public BalancedClickhouseDataSource(final String url, Map<SettingKey, Serializable> settings) {
        this(splitUrl(url), settings);
    }

    private BalancedClickhouseDataSource(final List<String> urls, Properties properties) {
        this(urls, ClickhouseJdbcUrlParser.parseProperties(properties));
    }

    private BalancedClickhouseDataSource(final List<String> urls, Map<SettingKey, Serializable> settings) {
        Validate.ensure(!urls.isEmpty(), "Incorrect ClickHouse jdbc url list. It must be not empty");

        this.cfg = ClickHouseConfig.Builder.builder()
                .withJdbcUrl(urls.get(0))
                .withSettings(settings)
                .host("undefined")
                .port(0)
                .build();

        List<String> allUrls = new ArrayList<>(urls.size());
        for (final String url : urls) {
            try {
                if (driver.acceptsURL(url)) {
                    allUrls.add(url);
                } else {
                    LOG.warn("that url is has not correct format: {}", url);
                }
            } catch (Exception e) {
                throw new InvalidValueException("error while checking url: " + url, e);
            }
        }

        Validate.ensure(!allUrls.isEmpty(), "there are no correct urls");

        this.allUrls = Collections.unmodifiableList(allUrls);
        this.enabledUrls = this.allUrls;
    }

    static List<String> splitUrl(final String url) {
        Matcher m = URL_TEMPLATE.matcher(url);
        Validate.ensure(m.matches(), "Incorrect url: " + url);
        final String database = StrUtil.getOrDefault(m.group(2), "");
        String[] hosts = m.group(1).split(",");
        return Arrays.stream(hosts)
                .map(host -> ClickhouseJdbcUrlParser.JDBC_CLICKHOUSE_PREFIX + "//" + host + database)
                .collect(Collectors.toList());
    }

    private boolean ping(final String url) {
        try (ClickHouseConnection connection = driver.connect(url, cfg)) {
            return connection.ping(Duration.ofSeconds(1));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if clickhouse on url is alive, if it isn't, disable url, else enable.
     *
     * @return number of available clickhouse urls
     */
    synchronized int actualize() {
        List<String> enabledUrls = new ArrayList<>(allUrls.size());

        for (String url : allUrls) {
            LOG.debug("Pinging disabled url: {}", url);
            if (ping(url)) {
                LOG.debug("Url is alive now: {}", url);
                enabledUrls.add(url);
            } else {
                LOG.warn("Url is dead now: {}", url);
            }
        }

        this.enabledUrls = Collections.unmodifiableList(enabledUrls);
        return enabledUrls.size();
    }


    private String getAnyUrl() throws SQLException {
        List<String> localEnabledUrls = enabledUrls;
        if (localEnabledUrls.isEmpty()) {
            throw new SQLException("Unable to get connection: there are no enabled urls");
        }
        Random random = this.randomThreadLocal.get();
        if (random == null) {
            this.randomThreadLocal.set(new Random());
            random = this.randomThreadLocal.get();
        }

        int index = random.nextInt(localEnabledUrls.size());
        return localEnabledUrls.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClickHouseConnection getConnection() throws SQLException {
        return driver.connect(getAnyUrl(), cfg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClickHouseConnection getConnection(String user, String password) throws SQLException {
        return driver.connect(getAnyUrl(), cfg.withCredentials(user, password));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return printWriter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        this.printWriter = printWriter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        loginTimeoutSeconds = seconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        return loginTimeoutSeconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    public List<String> getAllClickhouseUrls() {
        return allUrls;
    }

    public List<String> getEnabledClickHouseUrls() {
        return enabledUrls;
    }

    public List<String> getDisabledUrls() {
        List<String> enabledUrls = this.enabledUrls;
        if (!hasDisabledUrls()) {
            return Collections.emptyList();
        }
        List<String> disabledUrls = new ArrayList<>(allUrls);
        disabledUrls.removeAll(enabledUrls);
        return disabledUrls;
    }

    public boolean hasDisabledUrls() {
        return allUrls.size() != enabledUrls.size();
    }

    public ClickHouseConfig getCfg() {
        return cfg;
    }
}
