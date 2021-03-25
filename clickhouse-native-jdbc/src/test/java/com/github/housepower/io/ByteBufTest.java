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

package com.github.housepower.io;

import com.github.housepower.misc.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteBufTest {

    @Test
    public void testRefCnt() {
        ByteBuf src = Unpooled.wrappedBuffer(new byte[]{1, 2, 3});
        assertEquals(1, src.refCnt());
        ByteBuf dst = NettyUtil.alloc().buffer();
        assertEquals(1, dst.refCnt());
        dst.writeBytes(src);
        assertEquals(1, src.refCnt());
        assertEquals(1, dst.refCnt());
        src.release();
        assertEquals(0, src.refCnt());
        dst.release();
        assertEquals(0, dst.refCnt());
    }
}
