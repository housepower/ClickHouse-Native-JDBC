package org.houseflys.jdbc.protocol;

import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;

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
