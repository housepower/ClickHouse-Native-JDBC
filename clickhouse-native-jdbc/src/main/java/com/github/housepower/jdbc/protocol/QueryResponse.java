/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.housepower.jdbc.protocol;

import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.misc.CheckedIterator;
import com.github.housepower.jdbc.misc.CheckedSupplier;

import java.sql.SQLException;
import java.util.function.Supplier;

public class QueryResponse {
    private final CheckedSupplier<Response, SQLException> responseSupplier;
    private Block header;
    private boolean atEnd;
    // Progress
    // Totals
    // Extremes
    // ProfileInfo
    // EndOfStream

    public QueryResponse(CheckedSupplier<Response, SQLException> responseSupplier) {
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
            Response response = responseSupplier.get();
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
