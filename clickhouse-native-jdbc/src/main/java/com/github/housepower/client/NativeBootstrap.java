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

package com.github.housepower.client;

import com.github.housepower.exception.ClickHouseClientException;
import com.github.housepower.misc.NettyUtil;
import com.github.housepower.network.RequestEncoder;
import com.github.housepower.network.ResponseDecoder;
import com.github.housepower.network.ResponseHandler;
import com.github.housepower.settings.ClickHouseConfig;
import com.github.housepower.settings.ClickHouseDefines;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class NativeBootstrap {

    public static final NativeBootstrap INSTANCE = new NativeBootstrap();

    private final Bootstrap bootstrap;
    private final EventLoopGroup workerGroup;

    public NativeBootstrap() {
        this.workerGroup = NettyUtil.createEventLoopGroup();
        this.bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NettyUtil.socketChannelClass())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.ALLOCATOR, NettyUtil.alloc())
                .option(ChannelOption.SO_RCVBUF, ClickHouseDefines.SOCKET_RECV_BUFFER_BYTES)
                .option(ChannelOption.SO_SNDBUF, ClickHouseDefines.SOCKET_SEND_BUFFER_BYTES)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("logging_handler", new LoggingHandler("packet", LogLevel.TRACE))
                                .addLast("request_encoder", new RequestEncoder())
                                .addLast("response_decoder", new ResponseDecoder())
                                .addLast("response_handler", new ResponseHandler())
                                .addLast("idle_state_handler", new IdleStateHandler(600, 600, 600));
                    }
                });
    }

    public NativeContext createConnection(ClickHouseConfig cfg) {
        Channel ch = connect(new InetSocketAddress(cfg.host(), cfg.port()));
        NativeConnection conn = new NativeConnection(ch, cfg);
        return conn.initChannel();
    }

    private Channel connect(SocketAddress address) {
        Channel channel;
        ChannelFuture f = this.bootstrap.connect(address);
        try {
            f.await(3000, TimeUnit.MILLISECONDS);
            if (f.isCancelled()) {
                throw new ClickHouseClientException("connect cancelled.", f.cause());
            } else if (!f.isSuccess()) {
                throw new ClickHouseClientException("connect failed.", f.cause());
            } else {
                channel = f.channel();
            }
        } catch (Exception e) {
            throw new ClickHouseClientException("can not connect to clickhouse-server.", e);
        }
        return channel;
    }

    public void stop() {
        this.workerGroup.shutdownGracefully();
    }
}
