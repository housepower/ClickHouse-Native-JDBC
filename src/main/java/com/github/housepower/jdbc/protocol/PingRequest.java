package com.github.housepower.jdbc.protocol;

import com.github.housepower.jdbc.serializer.BinarySerializer;

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
