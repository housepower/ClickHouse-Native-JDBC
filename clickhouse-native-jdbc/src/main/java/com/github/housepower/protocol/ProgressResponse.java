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

import io.netty.buffer.ByteBuf;

public class ProgressResponse implements Response {

    public static ProgressResponse readFrom(ByteBuf buf) {
        return new ProgressResponse(helper.readVarInt(buf), helper.readVarInt(buf), helper.readVarInt(buf));
    }

    private final long newRows;
    private final long newBytes;
    private final long newTotalRows;

    public ProgressResponse(long newRows, long newBytes, long newTotalRows) {
        this.newRows = newRows;
        this.newBytes = newBytes;
        this.newTotalRows = newTotalRows;
    }

    @Override
    public ProtoType type() {
        return ProtoType.RESPONSE_PROGRESS;
    }

    public long newRows() {
        return newRows;
    }

    public long newBytes() {
        return newBytes;
    }

    public long newTotalRows() {
        return newTotalRows;
    }
}
