package org.houseflys.jdbc;

import org.houseflys.jdbc.connect.PhysicalConnection;
import org.houseflys.jdbc.connect.PhysicalInfo;
import org.houseflys.jdbc.connect.PhysicalInfo.ServerInfo;
import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.protocol.*;
import org.houseflys.jdbc.protocol.QueryRequest.ClientInfo;
import org.houseflys.jdbc.settings.ClickHouseConfig;
import org.houseflys.jdbc.wrapper.SQLConnection;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.houseflys.jdbc.protocol.QueryRequest.COMPLETE_STAGE;
import static org.houseflys.jdbc.settings.ClickHouseDefines.*;

public class ClickHouseConnection extends SQLConnection {

    private final ClickHouseConfig configure;
    // Just to be variable
    private final AtomicReference<PhysicalInfo> atomicInfo;

    protected ClickHouseConnection(ClickHouseConfig configure, PhysicalInfo atomicInfo) {
        this.configure = configure;
        this.atomicInfo = new AtomicReference<PhysicalInfo>(atomicInfo);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return new ClickHouseStatement(this);
    }

    @Override
    public void close() throws SQLException {
        atomicInfo.get().connection().disPhysicalConnection();
    }

    public QueryResponse sendQueryRequest(String query) throws SQLException {
        return sendQueryRequest(UUID.randomUUID().toString(), COMPLETE_STAGE, atomicInfo.get().client(), query);
    }

    public QueryResponse sendQueryRequest(String id, int stage, ClientInfo info, String query) throws SQLException {
        PhysicalConnection physical = getKeepAlivePhysicalConnection();
        return (QueryResponse) physical.sendRequest(new QueryRequest(id, info, stage, true, query));
    }

    private PhysicalConnection getKeepAlivePhysicalConnection() throws SQLException {
        PhysicalInfo info = atomicInfo.get();

        try {
            PhysicalConnection physical = info.connection();
            RequestOrResponse response = physical.sendRequest(new PingRequest());
            Validate.isTrue(response instanceof PongResponse, "Expect Pong Response.");
            return physical;
        } catch (SQLException ex) {
            PhysicalConnection physical = PhysicalConnection.openPhysicalConnection(configure);
            try {
                boolean successfully = atomicInfo.compareAndSet(
                    info, new PhysicalInfo(clientInfo(physical, configure), serverInfo(physical, configure), physical));
                if (successfully)
                    info.connection().disPhysicalConnection();
                return atomicInfo.get().connection();
            } catch (SQLException reThrows) {
                physical.disPhysicalConnection();
                throw reThrows;
            }
        }
    }

    public static ClickHouseConnection createClickHouseConnection(ClickHouseConfig configure) throws SQLException {
        PhysicalConnection physical = PhysicalConnection.openPhysicalConnection(configure);

        try {
            return new ClickHouseConnection(configure,
                new PhysicalInfo(clientInfo(physical, configure), serverInfo(physical, configure), physical));
        } catch (SQLException reThrows) {
            physical.disPhysicalConnection();
            throw reThrows;
        }
    }

    private static ClientInfo clientInfo(PhysicalConnection physical, ClickHouseConfig configure) {
        String clientName = String.format("%s %s", DBMS_NAME.stringValue(), "client");

        if (physical.address() instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) physical.address();
            String initialAddress = String.format("%s:%d", address.getHostName(), address.getPort());
            return new ClientInfo(
                ClientInfo.INITIAL_QUERY, "", "", initialAddress, "", address.getHostName(), clientName,
                DBMS_VERSION_MAJOR.longValue(), DBMS_VERSION_MINOR.longValue(), DBMS_CLIENT_REVERSION.longValue(), ""
            );
        }
        return new ClientInfo(
            ClientInfo.INITIAL_QUERY, "", "", "127.0.0.1:0", "", "127.0.0.1", clientName,
            DBMS_VERSION_MAJOR.longValue(), DBMS_VERSION_MINOR.longValue(), DBMS_CLIENT_REVERSION.longValue(), ""
        );
    }

    private static ServerInfo serverInfo(PhysicalConnection physical, ClickHouseConfig configure) throws SQLException {
        try {
            HelloRequest request = new HelloRequest("client", DBMS_CLIENT_REVERSION.longValue(),
                configure.database(), configure.username(), configure.password());

            RequestOrResponse response = physical.sendRequest(request);
            Validate.isTrue(response instanceof HelloResponse, "Expect Hello Response.");
            HelloResponse helloResponse = (HelloResponse) response;
            return new ServerInfo(
                helloResponse.reversion(),
                TimeZone.getTimeZone(helloResponse.serverTimeZone()), helloResponse.serverDisplayName());
        } catch (SQLException ex) {
            physical.disPhysicalConnection();
            throw ex;
        }
    }
}
