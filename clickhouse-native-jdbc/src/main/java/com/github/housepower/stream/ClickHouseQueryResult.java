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

package com.github.housepower.stream;

import com.github.housepower.data.Block;
import com.github.housepower.misc.CheckedIterator;
import com.github.housepower.misc.CheckedSupplier;
import com.github.housepower.protocol.DataResponse;
import com.github.housepower.protocol.EOFStreamResponse;
import com.github.housepower.protocol.Response;

import java.sql.SQLException;

public class ClickHouseQueryResult implements QueryResult {

    private final CheckedSupplier<Response, SQLException> responseSupplier;
    private Block header;
    private boolean atEnd;
    // Progress
    // Totals
    // Extremes
    // ProfileInfo
    // EndOfStream

    public ClickHouseQueryResult(CheckedSupplier<Response, SQLException> responseSupplier) {
        this.responseSupplier = responseSupplier;
    }

    @Override
    public Block header() throws SQLException {
        ensureHeaderConsumed();
        return header;
    }

    @Override
    public CheckedIterator<DataResponse, SQLException> data() {
        return new CheckedIterator<DataResponse, SQLException>() {

            private DataResponse current;

            @Override
            public boolean hasNext() throws SQLException {
                return current != null || fill() != null;
            }

            @Override
            public DataResponse next() throws SQLException {
                return drain();
            }

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
        };
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
}
