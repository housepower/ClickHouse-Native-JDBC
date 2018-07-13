package com.github.housepower.jdbc;

import com.github.housepower.jdbc.connect.PhysicalConnection;
import com.github.housepower.jdbc.connect.PhysicalInfo;
import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.protocol.EOFStreamResponse;
import com.github.housepower.jdbc.protocol.HelloResponse;
import com.github.housepower.jdbc.protocol.QueryRequest;
import com.github.housepower.jdbc.protocol.QueryResponse;
import com.github.housepower.jdbc.protocol.RequestOrResponse;
import com.github.housepower.jdbc.settings.ClickHouseConfig;
import com.github.housepower.jdbc.settings.ClickHouseDefines;
import com.github.housepower.jdbc.statement.ClickHousePreparedInsertStatement;
import com.github.housepower.jdbc.statement.ClickHousePreparedQueryStatement;
import com.github.housepower.jdbc.statement.ClickHouseStatement;
import com.github.housepower.jdbc.stream.InputFormat;
import com.github.housepower.jdbc.wrapper.SQLConnection;

import java.net.InetSocketAddress;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickHouseConnection extends SQLConnection {

    private static final Pattern VALUES_REGEX = Pattern.compile("[V|v][A|a][L|l][U|u][E|e][S|s]\\s*\\(");

    // Just to be variable
    private final AtomicBoolean isClosed;
    private final ClickHouseConfig configure;
    private final AtomicReference<PhysicalInfo> atomicInfo;

    protected ClickHouseConnection(ClickHouseConfig configure, PhysicalInfo info) {
        this.isClosed = new AtomicBoolean(false);
        this.configure = configure;
        this.atomicInfo = new AtomicReference<PhysicalInfo>(info);
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
        Validate.isTrue(!isClosed(), "Unable to create Statement, Because the connection is closed.");
        return new ClickHouseStatement(this);
    }

    @Override
    public PreparedStatement prepareStatement(String query) throws SQLException {
        Validate.isTrue(!isClosed(), "Unable to create PreparedStatement, Because the connection is closed.");
        Matcher matcher = VALUES_REGEX.matcher(query);
        return matcher.find() ? new ClickHousePreparedInsertStatement(matcher.end() - 1, query, this) :
            new ClickHousePreparedQueryStatement(this, query);
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
        Validate.isTrue(!isClosed(), "Unable to create Array, Because the connection is closed.");
        return new ClickHouseArray(elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        Validate.isTrue(!isClosed(), "Unable to create Struct, Because the connection is closed.");
        return new ClickHouseStruct(typeName, attributes);
    }

    public QueryResponse sendQueryRequest(final String query) throws SQLException {
        PhysicalConnection connection = getHealthyPhysicalConnection();

        connection.sendQuery(query, atomicInfo.get().client(), configure.settings());
        List<RequestOrResponse> data = new ArrayList<RequestOrResponse>();

        while (true) {
            RequestOrResponse response =
                connection.receiveResponse(configure.queryTimeout(), atomicInfo.get().server());

            if (response instanceof EOFStreamResponse) {
                return new QueryResponse(data);
            }

            data.add(response);
        }
    }

    public Integer sendInsertRequest(final String insertQuery, final InputFormat input) throws SQLException {
        PhysicalConnection connection = getHealthyPhysicalConnection();

        int rows = 0;
        connection.sendQuery(insertQuery, atomicInfo.get().client(), configure.settings());
        Block header = connection.receiveSampleBlock(configure.queryTimeout(), atomicInfo.get().server());

        while (true) {
            Block block = input.next(header, 8192);

            if (block.rows() == 0) {
                connection.sendData(new Block());
                connection.receiveEndOfStream(configure.queryTimeout(), atomicInfo.get().server());
                return rows;
            }

            connection.sendData(block);
            rows += block.rows();
        }
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
        String clientName = String.format("%s %s", ClickHouseDefines.NAME, "client");
        String initialAddress = "[::ffff:127.0.0.1]:0";
        return new QueryRequest.ClientInfo(initialAddress, address.getHostName(), clientName);
    }

    private static PhysicalInfo.ServerInfo serverInfo(PhysicalConnection physical, ClickHouseConfig configure) throws SQLException {
        try {
            long reversion = ClickHouseDefines.CLIENT_REVERSION;
            physical.sendHello("client", reversion, configure.database(), configure.username(), configure.password());

            HelloResponse response = physical.receiveHello(configure.queryTimeout(), null);
            TimeZone timeZone = TimeZone.getTimeZone(response.serverTimeZone());
            return new PhysicalInfo.ServerInfo(configure, response.reversion(), timeZone, response.serverDisplayName());
        } catch (SQLException rethrows) {
            physical.disPhysicalConnection();
            throw rethrows;
        }
    }
}
