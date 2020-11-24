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

import com.github.housepower.jdbc.connect.NativeClient;
import com.github.housepower.jdbc.connect.NativeContext;
import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.protocol.HelloResponse;
import com.github.housepower.jdbc.protocol.QueryRequest;
import com.github.housepower.jdbc.protocol.QueryResponse;
import com.github.housepower.jdbc.settings.ClickHouseConfig;
import com.github.housepower.jdbc.settings.ClickHouseDefines;
import com.github.housepower.jdbc.statement.ClickHousePreparedInsertStatement;
import com.github.housepower.jdbc.statement.ClickHousePreparedQueryStatement;
import com.github.housepower.jdbc.statement.ClickHouseStatement;
import com.github.housepower.jdbc.wrapper.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.sql.*;
import java.time.Duration;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickHouseConnection implements SQLConnection {

    private static final Logger LOG = LoggerFactory.getLogger(ClickHouseConnection.class);
    private static final Pattern VALUES_REGEX = Pattern.compile("[Vv][Aa][Ll][Uu][Ee][Ss]\\s*\\(");

    private final AtomicBoolean isClosed;
    private final AtomicReference<ClickHouseConfig> cfg;
    // TODO Since #getHealthyNativeClient() is synchronized, can we remove AtomicReference?
    private final AtomicReference<NativeContext> nativeCtx;
    // TODO move to NativeClient
    private final AtomicReference<ConnectionState> state = new AtomicReference<>(ConnectionState.IDLE);

    protected ClickHouseConnection(ClickHouseConfig cfg, NativeContext nativeCtx) {
        this.isClosed = new AtomicBoolean(false);
        this.cfg = new AtomicReference<>(cfg);
        this.nativeCtx = new AtomicReference<>(nativeCtx);
    }

    public ClickHouseConfig getCfg() {
        return cfg.get();
    }

    @Override
    public void close() throws SQLException {
        if (!isClosed() && isClosed.compareAndSet(false, true)) {
            NativeClient nativeClient = nativeCtx.get().nativeClient();
            nativeClient.disconnect();
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return isClosed.get();
    }

    @Override
    public Statement createStatement() throws SQLException {
        Validate.isTrue(!isClosed(), "Unable to create Statement, because the connection is closed.");
        return new ClickHouseStatement(this, nativeCtx.get());
    }

    @Override
    public PreparedStatement prepareStatement(String query) throws SQLException {
        Validate.isTrue(!isClosed(), "Unable to create PreparedStatement, because the connection is closed.");
        Matcher matcher = VALUES_REGEX.matcher(query);
        return matcher.find() ? new ClickHousePreparedInsertStatement(matcher.end() - 1, query, this, nativeCtx.get()) :
                new ClickHousePreparedQueryStatement(this, nativeCtx.get(), query);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.prepareStatement(sql);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        try {
            cfg.set(ClickHouseConfig.Builder.builder(cfg.get()).withProperties(properties).build());
        } catch (Exception ex) {
            Map<String, ClientInfoStatus> failed = new HashMap<>();
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                failed.put((String) entry.getKey(), ClientInfoStatus.REASON_UNKNOWN);
            }
            throw new SQLClientInfoException(failed, ex);
        }
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        Properties properties = new Properties();
        properties.put(name, value);
        this.setClientInfo(properties);
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        Validate.isTrue(!isClosed(), "Unable to create Array, because the connection is closed.");
        return new ClickHouseArray(elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        Validate.isTrue(!isClosed(), "Unable to create Struct, because the connection is closed.");
        return new ClickHouseStruct(typeName, attributes);
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return getNativeClient().ping(Duration.ofSeconds(timeout), nativeCtx.get().serverCtx());
    }

    // ClickHouse support only `database`, we treat it as JDBC `catalog`
    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.cfg.set(this.getCfg().withDatabase(catalog));
    }

    @Override
    public String getCatalog() throws SQLException {
        return this.getCfg().database();
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        // do nothing
    }

    @Override
    @Nullable
    public String getSchema() throws SQLException {
        return null;
    }

    public boolean ping(Duration timeout) throws SQLException {
        return nativeCtx.get().nativeClient().ping(timeout, nativeCtx.get().serverCtx());
    }

    public Block getSampleBlock(final String insertQuery) throws SQLException {
        NativeClient nativeClient = getHealthyNativeClient();
        nativeClient.sendQuery(insertQuery, nativeCtx.get().clientCtx(), cfg.get().settings());
        Validate.isTrue(this.state.compareAndSet(ConnectionState.IDLE, ConnectionState.WAITING_INSERT),
                "Connection is currently waiting for an insert operation, check your previous InsertStatement.");
        return nativeClient.receiveSampleBlock(cfg.get().queryTimeout(), nativeCtx.get().serverCtx());
    }

    public QueryResponse sendQueryRequest(final String query, ClickHouseConfig cfg) throws SQLException {
        Validate.isTrue(this.state.get() == ConnectionState.IDLE,
                "Connection is currently waiting for an insert operation, check your previous InsertStatement.");
        NativeClient nativeClient = getHealthyNativeClient();
        nativeClient.sendQuery(query, nativeCtx.get().clientCtx(), cfg.settings());
        return nativeClient.receiveQuery(cfg.queryTimeout(), nativeCtx.get().serverCtx());
    }

    // when sendInsertRequest we must ensure the connection is healthy
    // the #getSampleBlock() must be called before this method
    public int sendInsertRequest(Block block) throws SQLException {
        Validate.isTrue(this.state.get() == ConnectionState.WAITING_INSERT,
                "Call getSampleBlock before insert.");

        NativeClient nativeClient = getNativeClient();
        nativeClient.sendData(block);
        nativeClient.sendData(new Block());
        nativeClient.receiveEndOfStream(cfg.get().queryTimeout(), nativeCtx.get().serverCtx());
        Validate.isTrue(this.state.compareAndSet(ConnectionState.WAITING_INSERT, ConnectionState.IDLE));
        return block.rows();
    }

    synchronized private NativeClient getHealthyNativeClient() throws SQLException {
        NativeContext oldInfo = nativeCtx.get();
        if (!oldInfo.nativeClient().ping(cfg.get().queryTimeout(), nativeCtx.get().serverCtx())) {
            LOG.warn("connection loss with state[{}], create new connection and reset state", state);
            NativeContext newInfo = createNativeContext(cfg.get());
            // TODO method is synchronized
            NativeContext closeableCtx = nativeCtx.compareAndSet(oldInfo, newInfo) ? oldInfo : newInfo;
            closeableCtx.nativeClient().disconnect();
            assert oldInfo == closeableCtx;
            state.set(ConnectionState.IDLE);
        }

        return nativeCtx.get().nativeClient();
    }

    private NativeClient getNativeClient() {
        return nativeCtx.get().nativeClient();
    }

    public static ClickHouseConnection createClickHouseConnection(ClickHouseConfig configure) throws SQLException {
        return new ClickHouseConnection(configure, createNativeContext(configure));
    }

    private static NativeContext createNativeContext(ClickHouseConfig configure) throws SQLException {
        NativeClient nativeClient = NativeClient.connect(configure);
        return new NativeContext(clientContext(nativeClient, configure), serverContext(nativeClient, configure), nativeClient);
    }

    private static QueryRequest.ClientContext clientContext(NativeClient nativeClient, ClickHouseConfig configure) throws SQLException {
        Validate.isTrue(nativeClient.address() instanceof InetSocketAddress);
        InetSocketAddress address = (InetSocketAddress) nativeClient.address();
        String clientName = String.format(Locale.ROOT, "%s %s", ClickHouseDefines.NAME, "client");
        String initialAddress = "[::ffff:127.0.0.1]:0";
        return new QueryRequest.ClientContext(initialAddress, address.getHostName(), clientName);
    }

    private static NativeContext.ServerContext serverContext(NativeClient nativeClient, ClickHouseConfig configure) throws SQLException {
        try {
            long revision = ClickHouseDefines.CLIENT_REVISION;
            nativeClient.sendHello("client", revision, configure.database(), configure.user(), configure.password());

            HelloResponse response = nativeClient.receiveHello(configure.queryTimeout(), null);
            ZoneId timeZone = ZoneId.of(response.serverTimeZone());
            return new NativeContext.ServerContext(configure, response.reversion(), timeZone, response.serverDisplayName());
        } catch (SQLException rethrows) {
            nativeClient.disconnect();
            throw rethrows;
        }
    }
}
