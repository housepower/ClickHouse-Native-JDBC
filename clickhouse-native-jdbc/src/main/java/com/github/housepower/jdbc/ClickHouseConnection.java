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

import com.github.housepower.client.NativeBootstrap;
import com.github.housepower.client.NativeContext;
import com.github.housepower.data.Block;
import com.github.housepower.data.DataTypeFactory;
import com.github.housepower.jdbc.statement.ClickHousePreparedInsertStatement;
import com.github.housepower.jdbc.statement.ClickHousePreparedQueryStatement;
import com.github.housepower.jdbc.statement.ClickHouseStatement;
import com.github.housepower.jdbc.wrapper.SQLConnection;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.misc.ExceptionUtil;
import com.github.housepower.misc.Validate;
import com.github.housepower.settings.ClickHouseConfig;
import com.github.housepower.settings.SettingKey;
import com.github.housepower.stream.QueryResult;

import javax.annotation.Nullable;
import java.sql.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickHouseConnection implements SQLConnection {

    private static final Logger LOG = LoggerFactory.getLogger(ClickHouseConnection.class);
    private static final Pattern VALUES_REGEX = Pattern.compile("[Vv][Aa][Ll][Uu][Ee][Ss]\\s*\\(");

    public static ClickHouseConnection createClickHouseConnection(ClickHouseConfig cfg) throws SQLException {
        return ExceptionUtil.rethrowSQLException(() -> {
            NativeContext nativeCtx = NativeBootstrap.INSTANCE.createConnection(cfg);
            return new ClickHouseConnection(cfg, nativeCtx);
        });
    }

    private final AtomicBoolean isClosed;
    private final AtomicReference<ClickHouseConfig> cfg;
    private final NativeContext nativeCtx;

    protected ClickHouseConnection(ClickHouseConfig cfg, NativeContext nativeCtx) {
        this.isClosed = new AtomicBoolean(false);
        this.cfg = new AtomicReference<>(cfg);
        this.nativeCtx = nativeCtx;
    }

    public ClickHouseConfig cfg() {
        return cfg.get();
    }

    public NativeContext.ServerContext serverContext() {
        return nativeCtx.serverCtx();
    }

    public NativeContext.ClientContext clientContext() {
        return nativeCtx.clientCtx();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return true;
    }

    @Override
    public void commit() throws SQLException {
    }

    @Override
    public void rollback() throws SQLException {
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
    }

    @Override
    public int getHoldability() throws SQLException {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        this.close();
    }

    @Override
    public void close() throws SQLException {
        if (!isClosed() && isClosed.compareAndSet(false, true)) {
            ExceptionUtil.rethrowSQLException(() -> nativeCtx.nativeConn().close());
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return isClosed.get();
    }

    @Override
    public Statement createStatement() throws SQLException {
        Validate.isTrue(!isClosed(), "Unable to create Statement, because the connection is closed.");
        return new ClickHouseStatement(this, nativeCtx);
    }

    @Override
    public PreparedStatement prepareStatement(String query) throws SQLException {
        Validate.isTrue(!isClosed(), "Unable to create PreparedStatement, because the connection is closed.");
        Matcher matcher = VALUES_REGEX.matcher(query);
        return matcher.find() ? new ClickHousePreparedInsertStatement(matcher.end() - 1, query, this, nativeCtx) :
                new ClickHousePreparedQueryStatement(this, nativeCtx, query);
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
        return new ClickHouseArray(DataTypeFactory.get(typeName, nativeCtx.serverCtx()), elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        Validate.isTrue(!isClosed(), "Unable to create Struct, because the connection is closed.");
        return new ClickHouseStruct(typeName, attributes);
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return ExceptionUtil.rethrowSQLException(() -> nativeCtx.nativeConn().syncPing(Duration.ofSeconds(timeout)));
    }

    // ClickHouse support only `database`, we treat it as JDBC `schema`
    @Override
    public void setSchema(String schema) throws SQLException {
        this.cfg.set(this.cfg().withDatabase(schema));
    }

    @Override
    @Nullable
    public String getSchema() throws SQLException {
        return this.cfg().database();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        // do nothing
    }

    @Override
    public String getCatalog() throws SQLException {
        return null;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_NONE;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new ClickHouseDatabaseMetadata(cfg().jdbcUrl(), this);
    }

    @Override
    public Logger logger() {
        return ClickHouseConnection.LOG;
    }

    public boolean ping(Duration timeout) throws SQLException {
        return ExceptionUtil.rethrowSQLException(() -> nativeCtx.nativeConn().syncPing(timeout));
    }

    public Block getSampleBlock(final String insertQuery) throws SQLException {
        return ExceptionUtil.rethrowSQLException(() -> nativeCtx.nativeConn().syncSampleBlock(insertQuery));
    }

    public QueryResult sendQueryRequest(final String query, Map<SettingKey, Object> settings) throws SQLException {
        return ExceptionUtil.rethrowSQLException(() -> nativeCtx.nativeConn().syncQuery(query, settings));
    }

    public int sendInsertRequest(Block block) throws SQLException {
        ExceptionUtil.rethrowSQLException(() -> nativeCtx.nativeConn().syncStore(block));
        return block.rowCnt();
    }
}
