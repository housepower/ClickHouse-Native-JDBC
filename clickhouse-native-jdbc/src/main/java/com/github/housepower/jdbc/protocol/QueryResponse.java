package com.github.housepower.jdbc.protocol;

import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.misc.CheckedIterator;
import com.github.housepower.jdbc.misc.CheckedSupplier;

import java.sql.SQLException;
import java.util.function.Supplier;

public class QueryResponse {
    private final CheckedSupplier<RequestOrResponse, SQLException> responseSupplier;
    private Block header;
    private boolean atEnd;
    // Progress
    // Totals
    // Extremes
    // ProfileInfo
    // EndOfStream

    public QueryResponse(CheckedSupplier<RequestOrResponse, SQLException> responseSupplier) {
        this.responseSupplier = responseSupplier;
    }

    public Block header() throws SQLException {
        ensureHeaderConsumed();

        return header;
    }

    private void ensureHeaderConsumed() throws SQLException {
        if (header == null) {
            DataResponse firstDataResponse = consumeDataResponse();
            header = firstDataResponse != null ? firstDataResponse.block() : new Block();
        }
    }

    private DataResponse consumeDataResponse() throws SQLException {
        while (!atEnd) {
            RequestOrResponse response = responseSupplier.get();
            if (response instanceof DataResponse) {
                return (DataResponse) response;
            } else if (response instanceof EOFStreamResponse || response == null) {
                atEnd = true;
            }
        }

        return null;
    }

    public Supplier<CheckedIterator<DataResponse, SQLException>> data() {
        return () -> new CheckedIterator<DataResponse, SQLException>() {
            DataResponse current;

            private DataResponse fill() throws SQLException {
                ensureHeaderConsumed();
                return current = consumeDataResponse();
            }

            private DataResponse drain() throws SQLException {
                if (current == null) {
                    fill();
                }

                DataResponse top = current;
                current = null;
                return top;
            }

            @Override
            public boolean hasNext() throws SQLException {
                return current != null || fill() != null;
            }

            @Override
            public DataResponse next() throws SQLException {
                return drain();
            }
        };
    }
}
