package org.houseflys.jdbc.protocol;

import java.io.IOException;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;

public class EOFStreamResponse extends RequestOrResponse {

    EOFStreamResponse() {
        super(ProtocolType.RESPONSE_EndOfStream);
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        throw new UnsupportedOperationException("EndOfStreamResponse Cannot write to Server.");
    }

    public static RequestOrResponse readFrom(BinaryDeserializer deserializer) {
        return new EOFStreamResponse();
    }
}
