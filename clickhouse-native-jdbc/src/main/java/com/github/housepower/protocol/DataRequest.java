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

import com.github.housepower.data.Block;
import io.netty.buffer.ByteBuf;

public class DataRequest implements Request {

    public static final DataRequest EMPTY = new DataRequest("", new Block());

    private final String name;
    private final Block block;

    public DataRequest(String name, Block block) {
        this.name = name;
        this.block = block;
    }

    @Override
    public ProtoType type() {
        return ProtoType.REQUEST_DATA;
    }

    @Override
    public void encode0(ByteBuf buf) {
        writeUTF8Binary(buf, name);
        // TODO compress
        // serializer.maybeEnableCompressed();
        block.encode(buf);
        // serializer.maybeDisableCompressed();
    }
}
