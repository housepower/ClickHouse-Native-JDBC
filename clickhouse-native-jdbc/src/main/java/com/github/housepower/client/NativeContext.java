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

package com.github.housepower.client;

import com.github.housepower.io.ByteBufHelper;
import com.github.housepower.protocol.Encodable;
import com.github.housepower.settings.ClickHouseConfig;
import com.github.housepower.settings.ClickHouseDefines;
import io.netty.buffer.ByteBuf;

import java.time.ZoneId;

public class NativeContext {

    private final ClientContext clientCtx;
    private final ServerContext serverCtx;
    private final NativeConnection nativeConn;

    public NativeContext(ClientContext clientCtx, ServerContext serverCtx, NativeConnection nativeConn) {
        this.clientCtx = clientCtx;
        this.serverCtx = serverCtx;
        this.nativeConn = nativeConn;
    }

    public ClientContext clientCtx() {
        return clientCtx;
    }

    public ServerContext serverCtx() {
        return serverCtx;
    }

    public NativeConnection nativeConn() {
        return nativeConn;
    }

    public static class ClientContext implements ByteBufHelper, Encodable {
        public static final int TCP_KINE = 1;

        public static final byte NO_QUERY = 0;
        public static final byte INITIAL_QUERY = 1;
        public static final byte SECONDARY_QUERY = 2;

        private final String clientName;
        private final String clientHostname;
        private final String initialAddress;

        public ClientContext(String initialAddress, String clientHostname, String clientName) {
            this.clientName = clientName;
            this.clientHostname = clientHostname;
            this.initialAddress = initialAddress;
        }

        @Override
        public void encode(ByteBuf buf) {
            writeVarInt(buf, ClientContext.INITIAL_QUERY);
            writeUTF8Binary(buf, "");
            writeUTF8Binary(buf, "");
            writeUTF8Binary(buf, initialAddress);
            // for TCP kind
            writeVarInt(buf, TCP_KINE);
            writeUTF8Binary(buf, "");
            writeUTF8Binary(buf, clientHostname);
            writeUTF8Binary(buf, clientName);
            writeVarInt(buf, ClickHouseDefines.MAJOR_VERSION);
            writeVarInt(buf, ClickHouseDefines.MINOR_VERSION);
            writeVarInt(buf, ClickHouseDefines.CLIENT_REVISION);
            writeUTF8Binary(buf, "");
        }
    }

    public static class ServerContext {
        private final long majorVersion;
        private final long minorVersion;
        private final long reversion;
        private final ZoneId timeZone;
        private final String displayName;
        private final ClickHouseConfig configure;

        public ServerContext(long majorVersion, long minorVersion, long reversion,
                             ClickHouseConfig configure,
                             ZoneId timeZone, String displayName) {
            this.majorVersion = majorVersion;
            this.minorVersion = minorVersion;
            this.reversion = reversion;
            this.configure = configure;
            this.timeZone = timeZone;
            this.displayName = displayName;
        }

        public long majorVersion() {
            return majorVersion;
        }

        public long minorVersion() {
            return minorVersion;
        }

        public long reversion() {
            return reversion;
        }

        public String version() {
            return majorVersion + "." + minorVersion + "." + reversion;
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
