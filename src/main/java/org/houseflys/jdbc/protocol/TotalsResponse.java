package org.houseflys.jdbc.protocol;

import java.io.IOException;
import java.sql.SQLException;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.data.Block;

public class TotalsResponse extends RequestOrResponse {

    private final String name;
    private final Block block;

    TotalsResponse(String name, Block block) {
        super(ProtocolType.RESPONSE_Totals);
        this.name = name;
        this.block = block;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        throw new UnsupportedOperationException("TotalsResponse Cannot write to Server.");
    }

    public static TotalsResponse readFrom(BinaryDeserializer deserializer) throws IOException, SQLException {
        return new TotalsResponse(deserializer.readStringBinary(), Block.readFrom(deserializer));
    }
}
