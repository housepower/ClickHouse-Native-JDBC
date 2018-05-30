package org.houseflys.jdbc.protocol;

import org.houseflys.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;

import static org.houseflys.jdbc.settings.ClickHouseDefines.DBMS_CLIENT_REVERSION;
import static org.houseflys.jdbc.settings.ClickHouseDefines.DBMS_VERSION_MAJOR;
import static org.houseflys.jdbc.settings.ClickHouseDefines.DBMS_VERSION_MINOR;

public class QueryRequest extends RequestOrResponse {

    public static final int COMPLETE_STAGE = 2;

    private final int stage;
    private final String queryId;
    private final String queryString;
    private final boolean compression;
    private final ClientInfo clientInfo;


    public QueryRequest(String queryId, ClientInfo clientInfo, int stage, boolean compression, String queryString) {
        super(ProtocolType.REQUEST_QUERY);

        this.stage = stage;
        this.queryId = queryId;
        this.clientInfo = clientInfo;
        this.compression = compression;
        this.queryString = queryString;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException, SQLException {
        serializer.writeStringBinary(queryId);
        clientInfo.writeTo(serializer);

        // empty settings
        serializer.writeStringBinary("");
        serializer.writeVarInt(stage);
        serializer.writeBoolean(compression);
        serializer.writeStringBinary(queryString);
        // empty data to server
        DataRequest.EMPTY.writeTo(serializer);

    }

    public static class ClientInfo {
        public static final int TCP_KINE = 1;

        public static final byte NO_QUERY = 0;
        public static final byte INITIAL_QUERY = 1;
        public static final byte SECONDARY_QUERY = 2;

        private final String clientName;
        private final String clientHostname;
        private final String initialAddress;

        public ClientInfo(String initialAddress, String clientHostname, String clientName) {
            this.clientName = clientName;
            this.clientHostname = clientHostname;
            this.initialAddress = initialAddress;
        }

        public void writeTo(BinarySerializer serializer) throws IOException {
            serializer.writeVarInt(ClientInfo.INITIAL_QUERY);
            serializer.writeStringBinary("");
            serializer.writeStringBinary("");
            serializer.writeStringBinary(initialAddress);

            // for TCP kind
            serializer.writeVarInt(TCP_KINE);
            serializer.writeStringBinary("");
            serializer.writeStringBinary(clientHostname);
            serializer.writeStringBinary(clientName);
            serializer.writeVarInt(DBMS_VERSION_MAJOR.longValue());
            serializer.writeVarInt(DBMS_VERSION_MINOR.longValue());
            serializer.writeVarInt(DBMS_CLIENT_REVERSION.longValue());
            serializer.writeStringBinary("");
        }
    }
}
