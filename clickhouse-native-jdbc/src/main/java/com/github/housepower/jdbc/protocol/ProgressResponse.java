package com.github.housepower.jdbc.protocol;

import java.io.IOException;

import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;

public class ProgressResponse extends RequestOrResponse {

    private final long newRows;
    private final long newBytes;
    private final long newTotalRows;

    public ProgressResponse(long newRows, long newBytes, long newTotalRows) {
        super(ProtocolType.RESPONSE_Progress);

        this.newRows = newRows;
        this.newBytes = newBytes;
        this.newTotalRows = newTotalRows;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        throw new UnsupportedOperationException("ProgressResponse Cannot write to Server.");
    }

    public static ProgressResponse readFrom(BinaryDeserializer deserializer) throws IOException {
        return new ProgressResponse(deserializer.readVarInt(), deserializer.readVarInt(), deserializer.readVarInt());
    }

    public long getNewRows() {
        return newRows;
    }

    public long getNewBytes() {
        return newBytes;
    }

    public long getNewTotalRows() {
        return newTotalRows;
    }
}
