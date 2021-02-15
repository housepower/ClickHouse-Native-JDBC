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

import com.github.housepower.data.Block;
import com.github.housepower.exception.ClickHouseException;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.misc.ChannelHelper;
import com.github.housepower.misc.ExceptionUtil;
import com.github.housepower.misc.Validate;
import com.github.housepower.protocol.*;
import com.github.housepower.settings.ClickHouseConfig;
import com.github.housepower.settings.ClickHouseErrCode;
import com.github.housepower.settings.SettingKey;
import com.github.housepower.stream.QueryResult;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class NativeConnection implements ChannelHelper {

    private static final Logger log = LoggerFactory.getLogger(NativeConnection.class);

    private Channel channel;
    private BlockingQueue<Response> responseQueue;
    private ClickHouseConfig cfg;

    public NativeConnection(Channel channel, ClickHouseConfig cfg) {
        this.channel = channel;
        this.responseQueue = newResponseQueue();
        this.cfg = cfg;
        initChannel();
    }

    private void initChannel() {
        Validate.ensure(channel.isActive());
        // Validate.ensure(stateAttr(channel).compareAndSet(SessionState.INIT, SessionState.CONNECTED));
        stateAttr(channel).set(SessionState.CONNECTED);
        setResponseQueue(channel, responseQueue);
    }

    public Future<Boolean> ping() {
        Validate.ensure(channel.isActive());
        PingRequest request = PingRequest.INSTANCE;
        ResultFuture resultFuture = new ResultFuture(request);
        setResultFuture(channel, resultFuture);
        sendRequest(request);
        return resultFuture
                .toCompletableFuture()
                .handle((response, throwable) -> {
                    boolean active = throwable == null;
                    if (active) Validate.ensure(state.compareAndSet(SessionState.IDLE, SessionState.IDLE)
                            || state.compareAndSet(SessionState.WAITING_INSERT, SessionState.IDLE));
                    assert response instanceof PongResponse;
                    return active;
                });
    }

    public boolean syncPing(Duration soTimeout) {
        try {
            return ping().get(soTimeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.debug("ping failed", e);
            return false;
        }
    }

    public Future<Block> sampleBlock(String query) {
        // check state
        // set future
        // update channel conf (optional)
        // send request
    }

    public Future<QueryResult> query(String id, int stage, String sql, Map<SettingKey, Object> settings) {
        Validate.ensure(channel.isActive());
        Validate.ensure(state.get() == SessionState.IDLE);
        NativeContext.ClientContext clientCtx = getClientCtx(channel);
        QueryRequest request = new QueryRequest(id, clientCtx, stage, false, sql, settings);
        ResultFuture resultFuture = new ResultFuture(request);
        setResultFuture(channel, resultFuture);
        sendRequest(request);
        return resultFuture
                .toCompletableFuture()
                .whenComplete((response, throwable) -> {
                    boolean authenticated = throwable == null;
                    if (authenticated) {
                        assert response instanceof HelloResponse;
                        setServerCtx(channel, serverContext((HelloResponse) response, cfg));
                        Validate.ensure(state.compareAndSet(SessionState.CONNECTED, SessionState.IDLE));
                    }
                })
                .thenApply(response -> ((DataResponse) response).block());
    }

    public Future<Boolean> data(Block block) {

    }

    Future<HelloResponse> hello(String name, long reversion, String db, String user, String password) {
        Validate.ensure(channel.isActive());
        Validate.ensure(state.get() == SessionState.CONNECTED);
        setClientCtx(channel, clientContext(channel));
        HelloRequest request = new HelloRequest(name, reversion, db, user, password);
        ResultFuture resultFuture = new ResultFuture(request);
        setResultFuture(channel, resultFuture);
        sendRequest(request);
        return resultFuture
                .toCompletableFuture()
                .whenComplete((response, throwable) -> {
                    boolean authenticated = throwable == null;
                    if (authenticated) {
                        assert response instanceof HelloResponse;
                        setServerCtx(channel, serverContext((HelloResponse) response, cfg));
                        Validate.ensure(state.compareAndSet(SessionState.CONNECTED, SessionState.IDLE));
                    }
                })
                .thenApply(response -> (HelloResponse) response);
    }

    void syncHello(String name, long reversion, String db, String user, String password) {
        try {
            hello(name, reversion, db, user, password).get();
        } catch (Exception rethrow) {
            int errCode = ClickHouseErrCode.AUTHENTICATION_FAILED.code();
            ClickHouseException ex = ExceptionUtil.recursiveFind(rethrow, ClickHouseException.class);
            if (ex != null)
                errCode = ex.code();
            throw new ClickHouseException(errCode, rethrow);
        }
    }

    void sendRequest(Request request) {
        channel.writeAndFlush(request);
    }

    static NativeContext.ClientContext clientContext(Channel ch) {
        String initialAddress = "[::ffff:127.0.0.1]:0";
        SocketAddress localAddr = ch.localAddress();
        String localHostName;
        if (localAddr instanceof InetSocketAddress) {
            localHostName = ((InetSocketAddress) ch.localAddress()).getHostName();
        } else {
            localHostName = localAddr.toString();
        }
        String clientName = "ClickHouse Native JDBC Client";
        return new NativeContext.ClientContext(initialAddress, localHostName, clientName);
    }

    static NativeContext.ServerContext serverContext(HelloResponse response, ClickHouseConfig configure) {
        ZoneId timeZone = ZoneId.of(response.serverTimeZone());
        return new NativeContext.ServerContext(
                response.majorVersion(), response.minorVersion(), response.reversion(),
                configure, timeZone, response.serverDisplayName());
    }
}
