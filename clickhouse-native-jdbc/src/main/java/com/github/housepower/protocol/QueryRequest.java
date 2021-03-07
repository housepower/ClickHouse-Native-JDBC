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
import com.github.housepower.serde.BinarySerializer;
import com.github.housepower.serde.SettingType;
import com.github.housepower.settings.SettingKey;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.io.Serializable;
import java.util.Map;

public class QueryRequest implements Request {

    // Only read/have been read the columns specified in the query.
    public static final int STAGE_FETCH_COLUMNS = 0;
    // Until the stage where the results of processing on different servers can be combined.
    public static final int STAGE_WITH_MERGEABLE_STATE = 1;
    // Completely.
    public static final int STAGE_COMPLETE = 2;
    // Until the stage where the aggregate functions were calculated and finalized.
    // It is used for auto distributed_group_by_no_merge optimization for distributed engine.
    // (See comments in StorageDistributed).
    public static final int STAGE_WITH_MERGEABLE_STATE_AFTER_AGGREGATION = 3;

    private final int stage;
    private final String queryId;
    private final String queryString;
    private final boolean compression;
    private final NativeContext.ClientContext clientContext;
    private final Map<SettingKey, Serializable> settings;

    public QueryRequest(String queryId, NativeContext.ClientContext clientContext, int stage, boolean compression, String queryString) {
        this(queryId, clientContext, stage, compression, queryString, new HashMap<>());
    }

    public QueryRequest(String queryId, NativeContext.ClientContext clientContext, int stage, boolean compression, String queryString,
                        Map<SettingKey, Serializable> settings) {

        this.stage = stage;
        this.queryId = queryId;
        this.settings = settings;
        this.clientContext = clientContext;
        this.compression = compression;
        this.queryString = queryString;
    }

    @Override
    public ProtoType type() {
        return ProtoType.REQUEST_QUERY;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException, SQLException {
        serializer.writeUTF8StringBinary(queryId);
        clientContext.writeTo(serializer);

        for (Map.Entry<SettingKey, Serializable> entry : settings.entrySet()) {
            serializer.writeUTF8StringBinary(entry.getKey().name());
            @SuppressWarnings("rawtypes")
            SettingType type = entry.getKey().type();
            //noinspection unchecked
            type.serializeSetting(serializer, entry.getValue());
        }
        serializer.writeUTF8StringBinary("");
        serializer.writeVarInt(stage);
        serializer.writeBoolean(compression);
        serializer.writeUTF8StringBinary(queryString);
        // empty data to server
        DataRequest.EMPTY.writeTo(serializer);

    }
}
