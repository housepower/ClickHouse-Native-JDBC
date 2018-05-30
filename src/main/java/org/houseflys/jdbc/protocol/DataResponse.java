package org.houseflys.jdbc.protocol;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.data.Block;

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

    public static DataResponse readFrom(BinaryDeserializer deserializer) throws IOException, SQLException {
        String name = deserializer.readStringBinary();

        deserializer.maybeEnableCompressed();
        Block block = Block.readFrom(deserializer);
        deserializer.maybeDisenableCompressed();

        return new DataResponse(name, block);
    }

    public Block block() {
        return block;
    }
}
