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

import com.github.housepower.jdbc.connect.PhysicalConnection;
import com.github.housepower.jdbc.connect.PhysicalInfo;
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

import java.net.InetSocketAddress;
import java.sql.*;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickHouseConnection extends SQLConnection {

    private static final Pattern VALUES_REGEX = Pattern.compile("[Vv][Aa][Ll][Uu][Ee][Ss]\\s*\\(");

    // Just to be variable
    private final AtomicBoolean isClosed;
    private final ClickHouseConfig configure;
    private final AtomicReference<PhysicalInfo> atomicInfo;
    private final AtomicReference<ConnectionState> state = new AtomicReference<>(ConnectionState.IDLE);

    protected ClickHouseConnection(ClickHouseConfig configure, PhysicalInfo info) {
        this.isClosed = new AtomicBoolean(false);
        this.configure = configure;
        this.atomicInfo = new AtomicReference<>(info);
    }

    public ClickHouseConfig getConfigure() {
        return configure;
    }

    @Override
    public void close() throws SQLException {
        if (!isClosed() && isClosed.compareAndSet(false, true)) {
            PhysicalConnection connection = atomicInfo.get().connection();
            connection.disPhysicalConnection();
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return isClosed.get();
    }

    @Override
    public Statement createStatement() throws SQLException {
        Validate.isTrue(!isClosed(), "Unable to create Statement, because the connection is closed.");
        return new ClickHouseStatement(this, atomicInfo.get());
    }

    @Override
    public PreparedStatement prepareStatement(String query) throws SQLException {
        Validate.isTrue(!isClosed(), "Unable to create PreparedStatement, because the connection is closed.");
        Matcher matcher = VALUES_REGEX.matcher(query);
        return matcher.find() ? new ClickHousePreparedInsertStatement(matcher.end() - 1, query, this, atomicInfo.get()) :
                new ClickHousePreparedQueryStatement(this, atomicInfo.get(), query);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.prepareStatement(sql);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        configure.parseJDBCProperties(properties);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        Properties properties = new Properties();
        properties.put(name, value);
        configure.parseJDBCProperties(properties);
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
        ClickHouseConfig validConfigure = configure.copy();
        validConfigure.setQueryTimeout(timeout * 1000);
        try (Connection connection = new ClickHouseConnection(validConfigure, atomicInfo.get());
             Statement statement = connection.createStatement()) {
            statement.execute("SELECT 1");
            return true;
        }
    }

    public Block getSampleBlock(final String insertQuery) throws SQLException {
        PhysicalConnection connection = getHealthyPhysicalConnection();
        connection.sendQuery(insertQuery, atomicInfo.get().client(), configure.settings());
        Validate.isTrue(this.state.compareAndSet(ConnectionState.IDLE, ConnectionState.WAITING_INSERT),
                "Connection is currently waiting for an insert operation, check your previous InsertStatement.");
        return connection.receiveSampleBlock(configure.queryTimeout(), atomicInfo.get().server());
    }

    public QueryResponse sendQueryRequest(final String query, ClickHouseConfig cfg) throws SQLException {
        Validate.isTrue(this.state.get() == ConnectionState.IDLE,
                "Connection is currently waiting for an insert operation, check your previous InsertStatement.");
        PhysicalConnection connection = getHealthyPhysicalConnection();
        connection.sendQuery(query, atomicInfo.get().client(), cfg.settings());

        return new QueryResponse(() -> connection.receiveResponse(configure.queryTimeout(), atomicInfo.get().server()));
    }

    // when sendInsertRequest we must ensure the connection is healthy
    // the #getSampleBlock() must be called before this method
    public int sendInsertRequest(Block block) throws SQLException {
        Validate.isTrue(this.state.get() == ConnectionState.WAITING_INSERT,
                "Call getSampleBlock before insert.");

        PhysicalConnection connection = getPhysicalConnection();
        connection.sendData(block);
        connection.sendData(new Block());
        connection.receiveEndOfStream(configure.queryTimeout(), atomicInfo.get().server());
        Validate.isTrue(this.state.compareAndSet(ConnectionState.WAITING_INSERT, ConnectionState.IDLE));
        return block.rows();
    }

    private PhysicalConnection getHealthyPhysicalConnection() throws SQLException {
        PhysicalInfo oldInfo = atomicInfo.get();
        if (!oldInfo.connection().ping(configure.queryTimeout(), atomicInfo.get().server())) {
            PhysicalInfo newInfo = createPhysicalInfo(configure);
            PhysicalInfo closeableInfo = atomicInfo.compareAndSet(oldInfo, newInfo) ? oldInfo : newInfo;
            closeableInfo.connection().disPhysicalConnection();
        }

        return atomicInfo.get().connection();
    }

    private PhysicalConnection getPhysicalConnection() {
        return atomicInfo.get().connection();
    }

    public static ClickHouseConnection createClickHouseConnection(ClickHouseConfig configure) throws SQLException {
        return new ClickHouseConnection(configure, createPhysicalInfo(configure));
    }

    private static PhysicalInfo createPhysicalInfo(ClickHouseConfig configure) throws SQLException {
        PhysicalConnection physical = PhysicalConnection.openPhysicalConnection(configure);
        return new PhysicalInfo(clientInfo(physical, configure), serverInfo(physical, configure), physical);
    }

    private static QueryRequest.ClientInfo clientInfo(PhysicalConnection physical, ClickHouseConfig configure) throws SQLException {
        Validate.isTrue(physical.address() instanceof InetSocketAddress);
        InetSocketAddress address = (InetSocketAddress) physical.address();
        String clientName = String.format(Locale.ROOT, "%s %s", ClickHouseDefines.NAME, "client");
        String initialAddress = "[::ffff:127.0.0.1]:0";
        return new QueryRequest.ClientInfo(initialAddress, address.getHostName(), clientName);
    }

    private static PhysicalInfo.ServerInfo serverInfo(PhysicalConnection physical, ClickHouseConfig configure) throws SQLException {
        try {
            long revision = ClickHouseDefines.CLIENT_REVISION;
            physical.sendHello("client", revision, configure.database(), configure.username(), configure.password());

            HelloResponse response = physical.receiveHello(configure.queryTimeout(), null);
            ZoneId timeZone = ZoneId.of(response.serverTimeZone());
            return new PhysicalInfo.ServerInfo(configure, response.reversion(), timeZone, response.serverDisplayName());
        } catch (SQLException rethrows) {
            physical.disPhysicalConnection();
            throw rethrows;
        }
    }
}
