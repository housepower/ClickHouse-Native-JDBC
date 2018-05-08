package org.houseflys.jdbc.protocol;

import java.io.IOException;
import java.sql.SQLException;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Block;

public class ExtremesResponse extends RequestOrResponse {

    private final String name;
    private final Block block;

    ExtremesResponse(String name, Block block) {
        super(ProtocolType.RESPONSE_Extremes);
        this.name = name;
        this.block = block;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        throw new UnsupportedOperationException("ExtremesResponse Cannot write to Server.");
    }

    public static ExtremesResponse readFrom(BinaryDeserializer deserializer) throws IOException, SQLException {
        return new ExtremesResponse(deserializer.readStringBinary(), Block.readFrom(deserializer));
    }
}
