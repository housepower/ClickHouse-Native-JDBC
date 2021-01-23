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

package com.github.housepower.protocol;

import com.github.housepower.client.NativeContext;
import com.github.housepower.exception.NotImplementedException;
import com.github.housepower.serde.BinaryDeserializer;

import java.io.IOException;
import java.sql.SQLException;

public interface Response {

    ProtoType type();

    static Response readFrom(BinaryDeserializer deserializer, NativeContext.ServerContext info) throws IOException, SQLException {
        switch ((int) deserializer.readVarInt()) {
            case 0:
                return HelloResponse.readFrom(deserializer);
            case 1:
                return DataResponse.readFrom(deserializer, info);
            case 2:
                throw ExceptionResponse.readExceptionFrom(deserializer);
            case 3:
                return ProgressResponse.readFrom(deserializer);
            case 4:
                return PongResponse.readFrom(deserializer);
            case 5:
                return EOFStreamResponse.readFrom(deserializer);
            case 6:
                return ProfileInfoResponse.readFrom(deserializer);
            case 7:
                return TotalsResponse.readFrom(deserializer, info);
            case 8:
                return ExtremesResponse.readFrom(deserializer, info);
            case 9:
                throw new NotImplementedException("RESPONSE_TABLES_STATUS_RESPONSE");
            default:
                throw new IllegalStateException("Accept the id of response that is not recognized by Server.");
        }
    }

    enum ProtoType {
        RESPONSE_HELLO(0),
        RESPONSE_DATA(1),
        RESPONSE_EXCEPTION(2),
        RESPONSE_PROGRESS(3),
        RESPONSE_PONG(4),
        RESPONSE_END_OF_STREAM(5),
        RESPONSE_PROFILE_INFO(6),
        RESPONSE_TOTALS(7),
        RESPONSE_EXTREMES(8),
        RESPONSE_TABLES_STATUS_RESPONSE(9);

        private final int id;

        ProtoType(int id) {
            this.id = id;
        }

        public long id() {
            return id;
        }
    }
}
