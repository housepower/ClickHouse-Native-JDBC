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

package com.github.housepower.jdbc.connect;

import com.github.housepower.jdbc.protocol.QueryRequest.ClientInfo;
import com.github.housepower.jdbc.settings.ClickHouseConfig;

import java.time.ZoneId;

public class PhysicalInfo {

    private final ClientInfo client;
    private final ServerInfo server;
    private final PhysicalConnection connection;

    public PhysicalInfo(ClientInfo client, ServerInfo server, PhysicalConnection connection) {
        this.client = client;
        this.server = server;
        this.connection = connection;
    }

    public ClientInfo client() {
        return client;
    }

    public ServerInfo server() {
        return server;
    }

    public PhysicalConnection connection() {
        return connection;
    }

    public static class ServerInfo {
        private final long reversion;
        private final ZoneId timeZone;
        private final String displayName;
        private final ClickHouseConfig configure;

        public ServerInfo(ClickHouseConfig configure, long reversion, ZoneId timeZone, String displayName) {
            this.configure = configure;
            this.reversion = reversion;
            this.timeZone = timeZone;
            this.displayName = displayName;
        }

        public long reversion() {
            return reversion;
        }

        public ZoneId timeZone() {
            return timeZone;
        }

        public String displayName() {
            return displayName;
        }

        public ClickHouseConfig getConfigure() {
            return configure;
        }
    }
}
