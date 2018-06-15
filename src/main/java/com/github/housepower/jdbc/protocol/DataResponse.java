package com.github.housepower.jdbc.protocol;

import com.github.housepower.jdbc.connect.PhysicalInfo;
import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;

public class DataResponse extends RequestOrResponse {

    private final String name;
    private final Block block;

    public DataResponse(String name, Block block) {
        super(ProtocolType.RESPONSE_Data);
        this.name = name;
        this.block = block;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        throw new UnsupportedOperationException("DataResponse Cannot write to Server.");
    }

    public static DataResponse readFrom(BinaryDeserializer deserializer, PhysicalInfo.ServerInfo info)
        throws IOException, SQLException {
        String name = deserializer.readStringBinary();

        deserializer.maybeEnableCompressed();
        Block block = Block.readFrom(deserializer, info);
        deserializer.maybeDisenableCompressed();

        return new DataResponse(name, block);
    }

    public Block block() {
        return block;
    }
}
