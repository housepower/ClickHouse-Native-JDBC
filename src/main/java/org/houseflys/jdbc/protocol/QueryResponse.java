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

    QueryResponse(Block header, List<DataResponse> data) {
        super(ProtocolType.RESPONSE_QUERY);

        this.data = data;
        this.header = header;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        throw new UnsupportedOperationException("QueryResponse Cannot write to Server.");
    }


    public static RequestOrResponse readFrom(BinaryDeserializer deserializer) throws IOException, SQLException {
        List<DataResponse> resultSet = new ArrayList<DataResponse>();

        while (true) {
            RequestOrResponse response = RequestOrResponse.readFrom(null, deserializer);

            if (response.type() == ProtocolType.RESPONSE_Data) {
                resultSet.add((DataResponse) response);
            } else if (response.type() == ProtocolType.RESPONSE_EndOfStream) {

                if (resultSet.isEmpty()) {
                    return new QueryResponse(null, resultSet);
                }

                return new QueryResponse(resultSet.remove(0).block(), resultSet);
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
