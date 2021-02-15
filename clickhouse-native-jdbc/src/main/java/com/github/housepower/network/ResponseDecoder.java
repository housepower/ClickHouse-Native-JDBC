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

package com.github.housepower.network;

import com.github.housepower.client.NativeContext;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.log.Logging;
import com.github.housepower.protocol.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.concurrent.Future;

public class ResponseDecoder extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(ResponseDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        NativeContext.ServerContext serverCtx = ctx.channel().attr(serverCtxAttrKey(ctx)).get();
        in.markReaderIndex();
        try {
            Response response = Response.readFrom(in, serverCtx);
            out.add(response);
        } catch (IndexOutOfBoundsException ex) {
            log.debug(ex.getMessage());
            in.resetReaderIndex();
        }
    }

    private AttributeKey<NativeContext.ServerContext> serverCtxAttrKey(ChannelHandlerContext ctx) {
        return AttributeKey.valueOf(ctx.name() + ":server_ctx");
    }
}
