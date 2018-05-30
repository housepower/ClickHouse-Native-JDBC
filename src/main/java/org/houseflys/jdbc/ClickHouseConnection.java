package org.houseflys.jdbc;

import org.houseflys.jdbc.connect.PhysicalConnection;
import org.houseflys.jdbc.connect.PhysicalInfo;
import org.houseflys.jdbc.connect.PhysicalInfo.ServerInfo;
import org.houseflys.jdbc.data.Block;
import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.protocol.*;
import org.houseflys.jdbc.protocol.QueryRequest.ClientInfo;
import org.houseflys.jdbc.settings.ClickHouseConfig;
import org.houseflys.jdbc.stream.InputFormat;
import org.houseflys.jdbc.wrapper.SQLConnection;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import static org.houseflys.jdbc.settings.ClickHouseDefines.*;

public class ClickHouseConnection extends SQLConnection {

    private final ClickHouseConfig configure;
    // Just to be variable
    private final AtomicReference<PhysicalInfo> atomicInfo;

    protected ClickHouseConnection(ClickHouseConfig configure, PhysicalInfo info) {
        this.configure = configure;
        this.atomicInfo = new AtomicReference<PhysicalInfo>(info);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return new ClickHouseStatement(this);
    }

    @Override
    public void close() throws SQLException {
        atomicInfo.get().connection().disPhysicalConnection();
    }

    QueryResponse sendQueryRequest(final String query) throws SQLException {
        return withHealthyPhysicalConnection(new WithPhysicalConnection<QueryResponse>() {
            @Override
            public QueryResponse apply(PhysicalConnection physical) throws SQLException {
                physical.sendQuery(query, atomicInfo.get().client());
                List<RequestOrResponse> data = new ArrayList<RequestOrResponse>();

                while (true) {
                    RequestOrResponse response = physical.receiveResponse(configure.soTimeout());

                    if (response instanceof EOFStreamResponse) {
                        return new QueryResponse(data);
                    }

                    data.add(response);
                }
            }
        });
    }

    Integer sendInsertRequest(final String insertQuery, final InputFormat input) throws SQLException {
        return withHealthyPhysicalConnection(new WithPhysicalConnection<Integer>() {
            @Override
            public Integer apply(PhysicalConnection physical) throws SQLException {
                physical.sendQuery(insertQuery, atomicInfo.get().client());
                Block header = physical.receiveSampleBlock(configure.soTimeout());
                
                for (int rows = 0; ; ) {
                    Block block = input.next(header, 8192);

                    if (block.rows() == 0) {
                        physical.sendData(new Block());
                        physical.receiveEndOfStream(configure.soTimeout());
                        return rows;
                    }

                    physical.sendData(block);
                    rows += block.rows();
                }
            }
        });
    }

    private <T> T withHealthyPhysicalConnection(WithPhysicalConnection<T> with) throws SQLException {
        PhysicalInfo oldInfo = atomicInfo.get();
        if (!oldInfo.connection().ping(configure.soTimeout())) {
            PhysicalInfo newInfo = createPhysicalInfo(configure);
            PhysicalInfo closeableInfo = atomicInfo.compareAndSet(oldInfo, newInfo) ? oldInfo : newInfo;
            closeableInfo.connection().disPhysicalConnection();
        }

        return with.apply(atomicInfo.get().connection());
    }

    public static ClickHouseConnection createClickHouseConnection(ClickHouseConfig configure) throws SQLException {
        return new ClickHouseConnection(configure, createPhysicalInfo(configure));
    }

    private static PhysicalInfo createPhysicalInfo(ClickHouseConfig configure) throws SQLException {
        PhysicalConnection physical = PhysicalConnection.openPhysicalConnection(configure);
        ClientInfo client = clientInfo(physical, configure);
        ServerInfo server = serverInfo(physical, configure);
        return new PhysicalInfo(client, server, physical);
    }

    private static ClientInfo clientInfo(PhysicalConnection physical, ClickHouseConfig configure) throws SQLException {
        Validate.isTrue(physical.address() instanceof InetSocketAddress);
        InetSocketAddress address = (InetSocketAddress) physical.address();
        String clientName = String.format("%s %s", DBMS_NAME.stringValue(), "client");
        String initialAddress = String.format("%s:%d", address.getHostName(), address.getPort());
        return new ClientInfo(initialAddress, address.getHostName(), clientName);
    }

    private static ServerInfo serverInfo(PhysicalConnection physical, ClickHouseConfig configure) throws SQLException {
        try {
            long reversion = DBMS_CLIENT_REVERSION.longValue();
            physical.sendHello("client", reversion, configure.database(), configure.username(), configure.password());

            HelloResponse response = physical.receiveHello(configure.soTimeout());
            TimeZone timeZone = TimeZone.getTimeZone(response.serverTimeZone());
            return new ServerInfo(response.reversion(), timeZone, response.serverDisplayName());
        } catch (SQLException rethrows) {
            physical.disPhysicalConnection();
            throw rethrows;
        }
    }

    interface WithPhysicalConnection<T> {
        T apply(PhysicalConnection physical) throws SQLException;
    }
}
