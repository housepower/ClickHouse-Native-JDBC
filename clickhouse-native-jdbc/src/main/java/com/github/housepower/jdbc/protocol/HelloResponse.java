package com.github.housepower.jdbc.protocol;

import java.io.IOException;
import java.util.TimeZone;

import com.github.housepower.jdbc.serializer.BinarySerializer;
import com.github.housepower.jdbc.settings.ClickHouseDefines;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;

public class HelloResponse extends RequestOrResponse {

    private final long reversion;
    private final long majorVersion;
    private final long minorVersion;
    private final String serverName;
    private final String serverTimeZone;
    private final String serverDisplayName;

    public HelloResponse(
        String serverName, long majorVersion, long minorVersion, long reversion, String serverTimeZone,
        String serverDisplayName) {
        super(ProtocolType.RESPONSE_HELLO);

        this.reversion = reversion;
        this.serverName = serverName;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.serverTimeZone = serverTimeZone;
        this.serverDisplayName = serverDisplayName;
    }

    public long reversion() {
        return reversion;
    }

    public long majorVersion() {
        return majorVersion;
    }

    public long minorVersion() {
        return minorVersion;
    }

    public String serverName() {
        return serverName;
    }

    public String serverTimeZone() {
        return serverTimeZone;
    }

    public String serverDisplayName() {
        return serverDisplayName;
    }


    @Override
    public void writeImpl(BinarySerializer serializer) {
        throw new UnsupportedOperationException("HelloResponse Cannot write to Server.");
    }

    public static HelloResponse readFrom(BinaryDeserializer deserializer) throws IOException {
        String name = deserializer.readStringBinary();
        long majorVersion = deserializer.readVarInt();
        long minorVersion = deserializer.readVarInt();
        long serverReversion = deserializer.readVarInt();
        String serverTimeZone = getTimeZone(deserializer, serverReversion);
        String serverDisplayName = getDisplayName(deserializer, serverReversion);

        return new HelloResponse(name, majorVersion, minorVersion, serverReversion, serverTimeZone, serverDisplayName);
    }

    private static String getTimeZone(BinaryDeserializer deserializer, long serverReversion) throws IOException {
        return serverReversion >= ClickHouseDefines.DBMS_MIN_REVISION_WITH_SERVER_TIMEZONE ?
            deserializer.readStringBinary() : TimeZone.getDefault().getID();
    }

    private static String getDisplayName(BinaryDeserializer deserializer, long serverReversion) throws IOException {
        return serverReversion >= ClickHouseDefines.DBMS_MIN_REVISION_WITH_SERVER_DISPLAY_NAME ?
            deserializer.readStringBinary() : "localhost";
    }
}
