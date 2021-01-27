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

package com.github.housepower.netty;

import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.protocol.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ResponseDecoder extends ByteToMessageDecoder implements ChannelHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.isReadable()) {
            in.markReaderIndex();
            try {
                Response response = Response.readFrom(in, getServerCtx(ctx.channel()));
                LOG.trace("{}[{}] recv {}", ctx.channel().id(), stateAttr(ctx.channel()).get(), response.type());
                out.add(response);
            } catch (IndexOutOfBoundsException ex) {
                // TODO detect max length
                LOG.debug("decode incomplete response: {}", ex.getMessage());
                in.resetReaderIndex();
                break;
            }
        }
    }
}
