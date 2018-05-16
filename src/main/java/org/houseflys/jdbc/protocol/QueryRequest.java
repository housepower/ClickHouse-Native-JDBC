package org.houseflys.jdbc.protocol;

import org.houseflys.jdbc.serializer.BinarySerializer;

import java.io.IOException;

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
    public void writeImpl(BinarySerializer serializer) throws IOException {
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


        private final byte queryKind;
        private final String initialUser;
        private final String initialQueryId;
        private final String initialAddress;

        private final String osUser;
        private final String clientHostname;
        private final String clientName;
        private final long clientMajorVersion;
        private final long clientMinorVersion;
        private final long clientReversion;
        private final String quotaKey;

        public ClientInfo(byte queryKind, String initialUser, String initialQueryId, String initialAddress,
            String osUser, String clientHostname, String clientName, long clientMajorVersion,
            long clientMinorVersion, long clientReversion, String quotaKey) {
            this.queryKind = queryKind;
            this.initialUser = initialUser;
            this.initialQueryId = initialQueryId;
            this.initialAddress = initialAddress;
            this.osUser = osUser;
            this.clientHostname = clientHostname;
            this.clientName = clientName;
            this.clientMajorVersion = clientMajorVersion;
            this.clientMinorVersion = clientMinorVersion;
            this.clientReversion = clientReversion;
            this.quotaKey = quotaKey;
        }

        public void writeTo(BinarySerializer serializer) throws IOException {
            serializer.writeVarInt(queryKind);
            serializer.writeStringBinary(initialUser);
            serializer.writeStringBinary(initialQueryId);
            serializer.writeStringBinary(initialAddress);

            // for TCP kind
            serializer.writeVarInt(TCP_KINE);
            serializer.writeStringBinary(osUser);
            serializer.writeStringBinary(clientHostname);
            serializer.writeStringBinary(clientName);
            serializer.writeVarInt(clientMajorVersion);
            serializer.writeVarInt(clientMinorVersion);
            serializer.writeVarInt(clientReversion);
            serializer.writeStringBinary(quotaKey);
        }
    }
}
