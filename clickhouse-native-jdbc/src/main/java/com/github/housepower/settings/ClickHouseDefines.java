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

package com.github.housepower.settings;

public class ClickHouseDefines {
    public static final String NAME = "ClickHouse";
    public static final String DEFAULT_CATALOG = "default";
    public static final String DEFAULT_DATABASE = "default";

    public static final int MAJOR_VERSION = 1;
    public static final int MINOR_VERSION = 1;
    public static final int CLIENT_REVISION = 54380;
    public static final int DBMS_MIN_REVISION_WITH_SERVER_TIMEZONE = 54058;
    public static final int DBMS_MIN_REVISION_WITH_SERVER_DISPLAY_NAME = 54372;

    public static final int MAX_BLOCK_BYTES = 1024 * 1024 * 10;
    public static int SOCKET_SEND_BUFFER_BYTES = 1024 * 1024;
    public static int SOCKET_RECV_BUFFER_BYTES = 1024 * 1024;

    public static int COLUMN_BUFFER_BYTES = 1024 * 1024;

    public static final int DATA_TYPE_CACHE_SIZE = 1024;

    public static final int COMPRESSION_HEADER_LENGTH = 9;
    public static final int CHECKSUM_LENGTH = 16;
}
