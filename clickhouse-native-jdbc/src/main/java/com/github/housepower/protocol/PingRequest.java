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

import java.io.IOException;

public class PingRequest implements Request {

    public static final PingRequest INSTANCE = new PingRequest();

    @Override
    public ProtoType type() {
        return ProtoType.REQUEST_PING;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {
        // Nothing
    }
}
