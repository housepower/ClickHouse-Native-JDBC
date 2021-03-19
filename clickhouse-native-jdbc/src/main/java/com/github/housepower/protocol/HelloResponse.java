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

import com.github.housepower.io.CompositeSource;
import com.github.housepower.settings.ClickHouseDefines;

import java.io.IOException;
import java.time.ZoneId;

public class HelloResponse implements Response {

    public static HelloResponse readFrom(CompositeSource source) throws IOException {
        String name = source.readUTF8Binary();
        long majorVersion = source.readVarInt();
        long minorVersion = source.readVarInt();
        long serverReversion = source.readVarInt();
        String serverTimeZone = getTimeZone(source, serverReversion);
        String serverDisplayName = getDisplayName(source, serverReversion);

        return new HelloResponse(name, majorVersion, minorVersion, serverReversion, serverTimeZone, serverDisplayName);
    }

    private static String getTimeZone(CompositeSource source, long serverReversion) {
        return serverReversion >= ClickHouseDefines.DBMS_MIN_REVISION_WITH_SERVER_TIMEZONE ?
                source.readUTF8Binary() : ZoneId.systemDefault().getId();
    }

    private static String getDisplayName(CompositeSource source, long serverReversion) {
        return serverReversion >= ClickHouseDefines.DBMS_MIN_REVISION_WITH_SERVER_DISPLAY_NAME ?
                source.readUTF8Binary() : "localhost";
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
