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

public enum ProtocolType {
    REQUEST_HELLO(0),
    REQUEST_QUERY(1),
    REQUEST_DATA(2),
    REQUEST_PING(4),

    RESPONSE_HELLO(0),
    RESPONSE_Data(1),
    RESPONSE_Exception(2),
    RESPONSE_Progress(3),
    RESPONSE_Pong(4),
    RESPONSE_EndOfStream(5),
    RESPONSE_ProfileInfo(6),
    RESPONSE_Totals(7),
    RESPONSE_Extremes(8),
    RESPONSE_TablesStatusResponse(9);

    private final int id;

    ProtocolType(int id) {
        this.id = id;
    }

    public long id() {
        return id;
    }
}
