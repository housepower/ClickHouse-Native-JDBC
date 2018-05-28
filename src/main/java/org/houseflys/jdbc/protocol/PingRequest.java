package org.houseflys.jdbc.protocol;

import org.houseflys.jdbc.serializer.BinarySerializer;

import java.io.IOException;

public class PingRequest extends RequestOrResponse {

    public PingRequest() {
        super(ProtocolType.REQUEST_PING);
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        //Nothing
    }
}
