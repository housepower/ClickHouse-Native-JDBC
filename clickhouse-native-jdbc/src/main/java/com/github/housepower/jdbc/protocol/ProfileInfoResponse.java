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

import com.github.housepower.jdbc.serde.BinaryDeserializer;

import java.io.IOException;
import java.sql.SQLException;

public class ProfileInfoResponse implements Response {

    @Override
    public ProtoType type() {
        return ProtoType.RESPONSE_PROFILE_INFO;
    }

    public static ProfileInfoResponse readFrom(BinaryDeserializer deserializer) throws IOException, SQLException {
        long rows = deserializer.readVarInt();
        long blocks = deserializer.readVarInt();
        long bytes = deserializer.readVarInt();
        long applied_limit = deserializer.readVarInt();
        long rows_before_limit = deserializer.readVarInt();
        boolean calculated_rows_before_limit = deserializer.readBoolean();

        return new ProfileInfoResponse();
    }
}
