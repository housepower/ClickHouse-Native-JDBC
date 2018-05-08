package org.houseflys.jdbc.protocol;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Block;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryResponse extends RequestOrResponse {

    // Sample Header
    private final Block header;
    private final List<DataResponse> data;
    // Progress
    // Totals
    // Extremes
    // ProfileInfo
    // EndOfStream

    QueryResponse(DataResponse header, List<DataResponse> data) {
        super(ProtocolType.RESPONSE_QUERY);

        this.data = data;
        this.header = header.block();
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        throw new UnsupportedOperationException("QueryResponse Cannot write to Server.");
    }


    public static RequestOrResponse readFrom(BinaryDeserializer deserializer) throws IOException, SQLException {
        List<DataResponse> data = new ArrayList<DataResponse>();

        while (true) {
            RequestOrResponse response = RequestOrResponse.readFrom(null, deserializer);

            switch (response.type()) {
                case RESPONSE_Data:
                    data.add((DataResponse) response);
                    break;
                case RESPONSE_Progress:
                    break;
                case RESPONSE_Totals:
                    break;
                case RESPONSE_Extremes:
                    break;
                case RESPONSE_ProfileInfo:
                    break;
                case RESPONSE_EndOfStream:
                    return new QueryResponse(data.remove(0), data);
            }
        }
    }

    public Block header() {
        return header;
    }

    public List<DataResponse> data() {
        return data;
    }
}
