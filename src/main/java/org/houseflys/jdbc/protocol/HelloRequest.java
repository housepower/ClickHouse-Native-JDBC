package org.houseflys.jdbc.protocol;

import java.io.IOException;

import org.houseflys.jdbc.settings.ClickHouseDefines;
import org.houseflys.jdbc.serializer.BinarySerializer;

public class HelloRequest extends RequestOrResponse {

    private final String clientName;
    private final long clientReversion;
    private final String defaultDatabase;
    private final String clientUsername;
    private final String clientPassword;

    public HelloRequest(String clientName, long clientReversion, String defaultDatabase, String clientUsername,
        String clientPassword) {
        super(ProtocolType.REQUEST_HELLO);
        this.clientName = clientName;
        this.clientReversion = clientReversion;
        this.defaultDatabase = defaultDatabase;
        this.clientUsername = clientUsername;
        this.clientPassword = clientPassword;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        serializer.writeStringBinary(ClickHouseDefines.DBMS_NAME + " " + clientName);
        serializer.writeVarInt(ClickHouseDefines.DBMS_VERSION_MAJOR.intValue());
        serializer.writeVarInt(ClickHouseDefines.DBMS_VERSION_MINOR.intValue());
        serializer.writeVarInt(clientReversion);
        serializer.writeStringBinary(defaultDatabase);
        serializer.writeStringBinary(clientUsername);
        serializer.writeStringBinary(clientPassword);
    }
}
