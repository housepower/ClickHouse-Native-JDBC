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

import com.github.housepower.client.ResultFuture;
import com.github.housepower.exception.ClickHouseException;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.misc.ChannelHelper;
import com.github.housepower.protocol.*;
import com.github.housepower.settings.ClickHouseErrCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Deprecated
public class ResponseHandler extends SimpleChannelInboundHandler<Response> implements ChannelHelper {

    private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        ResultFuture resultFuture = getResultFuture(ctx.channel());
        Request request = resultFuture.getRequest();
        switch (request.type()) {
            case REQUEST_HELLO:
                if (response instanceof HelloResponse) {
                    resultFuture.toCompletableFuture().complete(response);
                } else {
                    resultFuture.toCompletableFuture().completeExceptionally(
                            new ClickHouseException(
                                    ClickHouseErrCode.UNEXPECTED_PACKET_FROM_SERVER.code(),
                                    response.type().toString()));
                }
                break;
            case REQUEST_QUERY:
                if (response instanceof DataResponse) {
                    resultFuture.toCompletableFuture().complete(response);
                } else {
                    log.debug("expect data, skip response: {}", response.type());
                }
                break;
            case REQUEST_DATA:
                if (response instanceof EOFStreamResponse) {
                    resultFuture.toCompletableFuture().complete(response);
                } else {
                    resultFuture.toCompletableFuture().completeExceptionally(
                            new ClickHouseException(
                                    ClickHouseErrCode.UNEXPECTED_PACKET_FROM_SERVER.code(),
                                    response.type().toString()));
                }
                break;
            case REQUEST_PING:
                if (response instanceof PongResponse) {
                    resultFuture.toCompletableFuture().complete(response);
                } else {
                    log.debug("expect pong, skip response: {}", response.type());
                }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        getResultFuture(ctx.channel()).toCompletableFuture().completeExceptionally(cause);
    }
}
