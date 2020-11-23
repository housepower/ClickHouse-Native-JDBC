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

import com.github.housepower.jdbc.protocol.QueryRequest.ClientContext;
import com.github.housepower.jdbc.settings.ClickHouseConfig;

import java.time.ZoneId;

public class NativeContext {

    private final ClientContext clientCtx;
    private final ServerContext serverCtx;
    private final NativeClient nativeClient;

    public NativeContext(ClientContext clientCtx, ServerContext serverCtx, NativeClient nativeClient) {
        this.clientCtx = clientCtx;
        this.serverCtx = serverCtx;
        this.nativeClient = nativeClient;
    }

    public ClientContext clientCtx() {
        return clientCtx;
    }

    public ServerContext serverCtx() {
        return serverCtx;
    }

    public NativeClient nativeClient() {
        return nativeClient;
    }

    public static class ServerContext {
        private final long reversion;
        private final ZoneId timeZone;
        private final String displayName;
        private final ClickHouseConfig configure;

        public ServerContext(ClickHouseConfig configure, long reversion, ZoneId timeZone, String displayName) {
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
