package com.github.housepower.jdbc.protocol;

import com.github.housepower.jdbc.data.Block;

import java.util.ArrayList;
import java.util.List;

public class QueryResponse {
    
    private final Block header;
    private final List<DataResponse> data;
    // Progress
    // Totals
    // Extremes
    // ProfileInfo
    // EndOfStream

    public QueryResponse(List<RequestOrResponse> responses) {
        List<DataResponse> dataResponses = new ArrayList<DataResponse>(responses.size());
        List<TotalsResponse> totalsResponses = new ArrayList<TotalsResponse>(responses.size());
        List<ProgressResponse> progressResponses = new ArrayList<ProgressResponse>(responses.size());
        List<ExtremesResponse> extremesResponses = new ArrayList<ExtremesResponse>(responses.size());
        List<ProfileInfoResponse> profileInfoResponses = new ArrayList<ProfileInfoResponse>(responses.size());

        for (RequestOrResponse response : responses) {
            if (response instanceof DataResponse) {
                dataResponses.add((DataResponse) response);
            } else if (response instanceof TotalsResponse) {
                totalsResponses.add((TotalsResponse) response);
            } else if (response instanceof ProgressResponse) {
                progressResponses.add((ProgressResponse) response);
            } else if (response instanceof ExtremesResponse) {
                extremesResponses.add((ExtremesResponse) response);
            } else if (response instanceof ProfileInfoResponse) {
                profileInfoResponses.add((ProfileInfoResponse) response);
            }
        }

        data = dataResponses;
        header = dataResponses.isEmpty() ? new Block() : dataResponses.remove(0).block();
    }

    public Block header() {
        return header;
    }

    public List<DataResponse> data() {
        return data;
    }
}
