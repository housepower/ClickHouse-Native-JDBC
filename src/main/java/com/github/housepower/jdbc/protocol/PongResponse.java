package com.github.housepower.jdbc.protocol;

import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;

public class PongResponse extends RequestOrResponse {
    PongResponse() {
        super(ProtocolType.RESPONSE_Pong);
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        throw new UnsupportedOperationException("PongResponse Cannot write to Server.");
    }

    public static PongResponse readFrom(BinaryDeserializer deserializer) throws IOException, SQLException {
        return new PongResponse();
    }
}
