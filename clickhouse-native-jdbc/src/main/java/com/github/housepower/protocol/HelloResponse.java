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

import com.github.housepower.settings.ClickHouseDefines;
import io.netty.buffer.ByteBuf;

import java.time.ZoneId;

public class HelloResponse implements Response {

    public static HelloResponse readFrom(ByteBuf buf) {
        String name = helper.readUTF8Binary(buf);
        long majorVersion = helper.readVarInt(buf);
        long minorVersion = helper.readVarInt(buf);
        long serverReversion = helper.readVarInt(buf);
        String serverTimeZone = getTimeZone(buf, serverReversion);
        String serverDisplayName = getDisplayName(buf, serverReversion);

        return new HelloResponse(name, majorVersion, minorVersion, serverReversion, serverTimeZone, serverDisplayName);
    }

    private static String getTimeZone(ByteBuf buf, long serverReversion) {
        return serverReversion >= ClickHouseDefines.DBMS_MIN_REVISION_WITH_SERVER_TIMEZONE ?
                helper.readUTF8Binary(buf) : ZoneId.systemDefault().getId();
    }

    private static String getDisplayName(ByteBuf buf, long serverReversion) {
        return serverReversion >= ClickHouseDefines.DBMS_MIN_REVISION_WITH_SERVER_DISPLAY_NAME ?
                helper.readUTF8Binary(buf) : "localhost";
    }

    private final long majorVersion;
    private final long minorVersion;
    private final long reversion;
    private final String serverName;
    private final String serverTimeZone;
    private final String serverDisplayName;

    public HelloResponse(
            String serverName, long majorVersion, long minorVersion, long reversion,
            String serverTimeZone,
            String serverDisplayName) {

        this.reversion = reversion;
        this.serverName = serverName;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.serverTimeZone = serverTimeZone;
        this.serverDisplayName = serverDisplayName;
    }

    @Override
    public ProtoType type() {
        return ProtoType.RESPONSE_HELLO;
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

    public String serverName() {
        return serverName;
    }

    public String serverTimeZone() {
        return serverTimeZone;
    }

    public String serverDisplayName() {
        return serverDisplayName;
    }
}
