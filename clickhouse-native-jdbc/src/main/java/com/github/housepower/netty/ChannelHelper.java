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

import com.github.housepower.client.NativeContext;
import com.github.housepower.protocol.Response;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import javax.annotation.Nullable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public interface ChannelHelper {

    AttributeKey<NativeContext.ClientContext> clientCtxAttrKey = AttributeKey.valueOf("client_ctx");
    AttributeKey<NativeContext.ServerContext> serverCtxAttrKey = AttributeKey.valueOf("server_ctx");
    AttributeKey<BlockingQueue<Response>> responseQueueAttrKey = AttributeKey.valueOf("response_queue");
    AttributeKey<ChannelState> stateAttrKey = AttributeKey.valueOf("state");

    default void setClientCtx(Channel ch, NativeContext.ClientContext clientCtx) {
        ch.attr(clientCtxAttrKey).set(clientCtx);
    }

    default NativeContext.ClientContext getClientCtx(Channel ch) {
        return ch.attr(clientCtxAttrKey).get();
    }

    default void setServerCtx(Channel ch, NativeContext.ServerContext serverCtx) {
        ch.attr(serverCtxAttrKey).set(serverCtx);
    }

    @Nullable
    default NativeContext.ServerContext getServerCtx(Channel ch) {
        return ch.attr(serverCtxAttrKey).get();
    }

    default void setResponseQueue(Channel ch, BlockingQueue<Response> queue) {
        ch.attr(responseQueueAttrKey).set(queue);
    }

    default BlockingQueue<Response> getResponseQueue(Channel ch) {
        return ch.attr(responseQueueAttrKey).get();
    }

    default Attribute<ChannelState> stateAttr(Channel ch) {
        return ch.attr(stateAttrKey);
    }

    default BlockingQueue<Response> newResponseQueue() {
        return new LinkedBlockingQueue<>(16);
    }
}
