package com.github.housepower.jdbc.protocol;

import java.io.IOException;
import java.sql.SQLException;

import com.github.housepower.jdbc.connect.PhysicalInfo;
import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;

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

    public static TotalsResponse readFrom(BinaryDeserializer deserializer, PhysicalInfo.ServerInfo info)
        throws IOException, SQLException {
        return new TotalsResponse(deserializer.readStringBinary(), Block.readFrom(deserializer, info));
    }
}
