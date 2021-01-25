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

import com.github.housepower.serde.BinarySerializer;
import com.github.housepower.settings.ClickHouseDefines;

import java.io.IOException;

public class HelloRequest implements Request {

    private final String clientName;
    private final long clientReversion;
    private final String defaultDatabase;
    private final String clientUsername;
    private final String clientPassword;

    public HelloRequest(String clientName, long clientReversion, String defaultDatabase,
                        String clientUsername, String clientPassword) {
        this.clientName = clientName;
        this.clientReversion = clientReversion;
        this.defaultDatabase = defaultDatabase;
        this.clientUsername = clientUsername;
        this.clientPassword = clientPassword;
    }

    @Override
    public ProtoType type() {
        return ProtoType.REQUEST_HELLO;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        serializer.writeUTF8StringBinary(ClickHouseDefines.NAME + " " + clientName);
        serializer.writeVarInt(ClickHouseDefines.MAJOR_VERSION);
        serializer.writeVarInt(ClickHouseDefines.MINOR_VERSION);
        serializer.writeVarInt(clientReversion);
        serializer.writeUTF8StringBinary(defaultDatabase);
        serializer.writeUTF8StringBinary(clientUsername);
        serializer.writeUTF8StringBinary(clientPassword);
    }
}
