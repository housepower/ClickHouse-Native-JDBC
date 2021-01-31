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

package com.github.housepower.buffer;

import com.github.housepower.misc.NettyUtil;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class ByteArrayWriter implements BuffedWriter {

    private final int columnSize;
    private final List<ByteBuf> bufList = new ArrayList<>();


    public ByteArrayWriter(int columnSize) {
        this.columnSize = columnSize;
    }

    @Override
    public void writeBinary(byte byt) {
        bufList.add(NettyUtil.alloc().buffer(1, 1).writeByte(byt));
    }

    @Override
    public void writeBinary(ByteBuf bytes) {
        bufList.add(bytes);
    }

    @Override
    public void flushToTarget(boolean force) {
    }

    public ByteBuf getBuf() {
        return NettyUtil.alloc().compositeBuffer(bufList.size()).addComponents(true, bufList);
    }
}
