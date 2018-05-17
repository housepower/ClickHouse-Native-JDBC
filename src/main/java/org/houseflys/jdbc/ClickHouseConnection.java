package org.houseflys.jdbc;

import static org.houseflys.jdbc.settings.ClickHouseDefines.DBMS_CLIENT_REVERSION;
import static org.houseflys.jdbc.settings.ClickHouseDefines.DBMS_NAME;
import static org.houseflys.jdbc.settings.ClickHouseDefines.DBMS_VERSION_MAJOR;
import static org.houseflys.jdbc.settings.ClickHouseDefines.DBMS_VERSION_MINOR;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.UUID;

import org.houseflys.jdbc.connect.PhysicalConnection;
import org.houseflys.jdbc.protocol.HelloRequest;
import org.houseflys.jdbc.protocol.HelloResponse;
import org.houseflys.jdbc.protocol.QueryRequest;
import org.houseflys.jdbc.protocol.QueryRequest.ClientInfo;
import org.houseflys.jdbc.protocol.QueryResponse;
import org.houseflys.jdbc.protocol.RequestOrResponse;
import org.houseflys.jdbc.settings.ClickHouseConfig;
import org.houseflys.jdbc.settings.ClickHouseURL;
import org.houseflys.jdbc.wrapper.SQLConnection;

public class ClickHouseConnection extends SQLConnection {

    private static final String CLIENT_NAME = "client";

    private final ClickHouseConfig configure;
    private final PhysicalConnection physicalConnection;

    public ClickHouseConnection(String url, Properties properties) throws SQLException {
        try {
            configure = new ClickHouseURL(url, properties).asConfig();
            physicalConnection = new PhysicalConnection(configure);

            this.connection();
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        return new ClickHouseStatement(this);
    }

    @Override
    public void close() throws SQLException {
        physicalConnection.close();
    }

    QueryResponse sendQueryRequest(String query) throws SQLException {
        try {
            return (QueryResponse) physicalConnection.sendRequest(new QueryRequest(
                UUID.randomUUID().toString(), getOrCreateClientInfo(), QueryRequest.COMPLETE_STAGE, true, query));
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    private void connection() throws IOException, SQLException {
        physicalConnection.connect();

        RequestOrResponse response = physicalConnection.sendRequest(
            new HelloRequest(CLIENT_NAME, DBMS_CLIENT_REVERSION.longValue(), configure.database(), configure.username(),
                configure.password()));

        if (response instanceof HelloResponse) {
            // TODO: global setting ?
            String serverTimeZone = ((HelloResponse) response).serverTimeZone();
            String serverDisplayName = ((HelloResponse) response).serverDisplayName();
        }
    }

    private ClientInfo getOrCreateClientInfo() throws UnknownHostException {
        return new ClientInfo(ClientInfo.INITIAL_QUERY, "", "",
            physicalConnection.localAddress() + ":" + physicalConnection.localPort(), "",
            physicalConnection.localAddress(), DBMS_NAME + " " + CLIENT_NAME, DBMS_VERSION_MAJOR.longValue(),
            DBMS_VERSION_MINOR.longValue(),
            DBMS_CLIENT_REVERSION.longValue(), "");
    }
}
