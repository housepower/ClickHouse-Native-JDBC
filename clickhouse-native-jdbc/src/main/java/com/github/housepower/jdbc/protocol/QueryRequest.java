package com.github.housepower.jdbc.protocol;

import com.github.housepower.jdbc.serializer.BinarySerializer;
import com.github.housepower.jdbc.settings.ClickHouseDefines;
import com.github.housepower.jdbc.settings.SettingKey;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class QueryRequest extends RequestOrResponse {

    public static final int COMPLETE_STAGE = 2;

    private final int stage;
    private final String queryId;
    private final String queryString;
    private final boolean compression;
    private final ClientInfo clientInfo;
    private final Map<SettingKey, Object> settings;

    public QueryRequest(String queryId, ClientInfo clientInfo, int stage, boolean compression, String queryString) {
        this(queryId, clientInfo, stage, compression, queryString, new HashMap<SettingKey, Object>());
    }

    public QueryRequest(String queryId, ClientInfo clientInfo, int stage, boolean compression, String queryString,
        Map<SettingKey, Object> settings) {
        super(ProtocolType.REQUEST_QUERY);

        this.stage = stage;
        this.queryId = queryId;
        this.settings = settings;
        this.clientInfo = clientInfo;
        this.compression = compression;
        this.queryString = queryString;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException, SQLException {
        serializer.writeStringBinary(queryId);
        clientInfo.writeTo(serializer);

        for (Map.Entry<SettingKey, Object> entry : settings.entrySet()) {
            serializer.writeStringBinary(entry.getKey().name());
            entry.getKey().type().serializeSetting(serializer, entry.getValue());
        }
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
            serializer.writeVarInt(ClickHouseDefines.MAJOR_VERSION);
            serializer.writeVarInt(ClickHouseDefines.MINOR_VERSION);
            serializer.writeVarInt(ClickHouseDefines.CLIENT_REVERSION);
            serializer.writeStringBinary("");
        }
    }
}
