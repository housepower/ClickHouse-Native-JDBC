package com.github.housepower.jdbc.protocol;

import java.io.IOException;
import java.sql.SQLException;

import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;

public class ProfileInfoResponse extends RequestOrResponse {

    ProfileInfoResponse() {
        super(ProtocolType.RESPONSE_ProfileInfo);
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        throw new UnsupportedOperationException("ProfileInfoResponse Cannot write to Server.");
    }

    public static ProfileInfoResponse readFrom(BinaryDeserializer deserializer) throws IOException, SQLException {
        long rows = deserializer.readVarInt();
        long blocks = deserializer.readVarInt();
        long bytes = deserializer.readVarInt();
        long applied_limit = deserializer.readVarInt();
        long rows_before_limit = deserializer.readVarInt();
        boolean calculated_rows_before_limit = deserializer.readBoolean();

        return new ProfileInfoResponse();
    }
}
