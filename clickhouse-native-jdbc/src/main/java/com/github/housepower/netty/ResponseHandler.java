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

import com.github.housepower.exception.ClickHouseException;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.exception.ExceptionUtil;
import com.github.housepower.protocol.ExceptionResponse;
import com.github.housepower.protocol.Response;
import com.github.housepower.settings.ClickHouseErrCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;

public class ResponseHandler extends SimpleChannelInboundHandler<Response> implements ChannelHelper {

    private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        BlockingQueue<Response> responseQueue = getResponseQueue(ctx.channel());
        responseQueue.put(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        BlockingQueue<Response> responseQueue = getResponseQueue(ctx.channel());
        int errCode = ClickHouseErrCode.UNKNOWN_ERROR.code();
        ClickHouseException ex = ExceptionUtil.recursiveFind(cause, ClickHouseException.class);
        if (ex != null)
            errCode = ex.errCode();
        responseQueue.put(new ExceptionResponse(new ClickHouseException(errCode, cause)));
    }
}
