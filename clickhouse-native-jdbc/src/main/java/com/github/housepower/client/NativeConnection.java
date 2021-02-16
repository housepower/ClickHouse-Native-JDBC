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
import com.github.housepower.exception.ClickHouseClientException;
import com.github.housepower.exception.ClickHouseException;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.misc.ChannelHelper;
import com.github.housepower.misc.ExceptionUtil;
import com.github.housepower.misc.Validate;
import com.github.housepower.protocol.*;
import com.github.housepower.settings.ClickHouseConfig;
import com.github.housepower.settings.ClickHouseDefines;
import com.github.housepower.settings.ClickHouseErrCode;
import com.github.housepower.settings.SettingKey;
import com.github.housepower.stream.ClickHouseQueryResult;
import com.github.housepower.stream.QueryResult;
import io.netty.channel.Channel;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class NativeConnection implements ChannelHelper, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(NativeConnection.class);

    private volatile Channel channel;
    private volatile BlockingQueue<Response> responseQueue;
    private ClickHouseConfig cfg;

    public NativeConnection(Channel channel, ClickHouseConfig cfg) {
        this.channel = channel;
        this.responseQueue = newResponseQueue();
        this.cfg = cfg;
    }

    public NativeContext initChannel() {
        stateAttr(channel).set(SessionState.INIT);
        Validate.ensure(channel.isActive());
        Validate.ensure(stateAttr(channel).compareAndSet(SessionState.INIT, SessionState.CONNECTED));
        NativeContext.ClientContext clientCtx = clientContext(channel);
        setClientCtx(channel, clientCtx);
        setResponseQueue(channel, responseQueue);
        stateAttr(channel).set(SessionState.CONNECTED);
        syncHello("ClickHouse-Native-JDBC", ClickHouseDefines.CLIENT_REVISION, cfg.database(), cfg.user(), cfg.password());
        NativeContext.ServerContext serverCtx = getServerCtx(channel);
        return new NativeContext(clientCtx, serverCtx, this);
    }

    void checkOrRepairChannel() {
        syncPing(Duration.ofMillis(1000));
        // TODO reconnect if current channel broken
    }

    @Override
    public void close() {

    }

    public void silentClose() {
        try {
            close();
        } catch (Throwable th) {
            log.debug("close throw exception", th);
        }
    }

    public Future<Boolean> ping() {
        Validate.ensure(channel.isActive());
        PingRequest request = PingRequest.INSTANCE;
        sendRequest(request);
        return CompletableFuture
                .supplyAsync(() -> recvResponse(PongResponse.class, true))
                .handle((response, throwable) -> {
                    boolean active = throwable == null;
                    if (active)
                        changeState(SessionState.IDLE, SessionState.WAITING_INSERT, SessionState.IDLE);
                    return active;
                });
    }

    public boolean syncPing(Duration timeout) {
        try {
            return ping().get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.debug("ping failed", e);
            return false;
        }
    }

    public Future<Block> sampleBlock(String sampleSql) {
        log.debug("sample sql: {}", sampleSql);
        checkOrRepairChannel();
        checkState(SessionState.IDLE);
        QueryRequest request = new QueryRequest(
                nextId(),
                getClientCtx(channel),
                QueryRequest.STAGE_COMPLETE,
                false, // TODO support compress
                sampleSql,
                cfg.settings());
        sendRequest(request);

        return CompletableFuture
                .supplyAsync(() -> recvResponse(DataResponse.class, true))
                .thenApply(dataResponse -> {
                    changeState(SessionState.IDLE, SessionState.WAITING_INSERT);
                    return dataResponse.block();
                });
    }

    public Block syncSampleBlock(String sampleSql) {
        try {
            return sampleBlock(sampleSql).get();
        } catch (Exception rethrow) {
            log.error("sample block failed\n=== failed sql ===\n{}\n===", sampleSql, rethrow);
            int errCode = ClickHouseErrCode.UNKNOWN_ERROR.code();
            ClickHouseException ex = ExceptionUtil.recursiveFind(rethrow, ClickHouseException.class);
            if (ex != null)
                errCode = ex.code();
            throw new ClickHouseException(errCode, rethrow);
        }
    }

    public Future<QueryResult> query(String querySql, Map<SettingKey, Object> settings) {
        checkOrRepairChannel();
        QueryRequest request = new QueryRequest(
                nextId(),
                getClientCtx(channel),
                QueryRequest.STAGE_COMPLETE,
                false, // TODO support compress
                querySql,
                settings);
        sendRequest(request);
        return CompletableFuture
                .supplyAsync(() -> new ClickHouseQueryResult(() ->
                        recvResponse(DataResponse.class, EOSResponse.class, Duration.ofMillis(300), false, true)));
    }

    public QueryResult syncQuery(String querySql, Map<SettingKey, Object> settings) {
        try {
            return query(querySql, settings).get();
        } catch (Exception rethrow) {
            log.error("query failed\n=== failed sql ===\n{}\n===", querySql, rethrow);
            int errCode = ClickHouseErrCode.UNKNOWN_ERROR.code();
            ClickHouseException ex = ExceptionUtil.recursiveFind(rethrow, ClickHouseException.class);
            if (ex != null)
                errCode = ex.code();
            throw new ClickHouseException(errCode, rethrow);
        }
    }

    public Future<Void> store(Block block) {
        checkState(SessionState.WAITING_INSERT);
        DataRequest request = new DataRequest("", block);
        sendRequest(request);
        sendRequest(DataRequest.EMPTY);
        return CompletableFuture
                .supplyAsync(() -> recvResponse(EOSResponse.class, false))
                .thenAccept(eos -> changeState(SessionState.WAITING_INSERT, SessionState.IDLE));
    }

    public void syncStore(Block block) {
        try {
            store(block).get();
        } catch (Exception rethrow) {
            log.error("store failed", rethrow);
            int errCode = ClickHouseErrCode.UNKNOWN_ERROR.code();
            ClickHouseException ex = ExceptionUtil.recursiveFind(rethrow, ClickHouseException.class);
            if (ex != null)
                errCode = ex.code();
            throw new ClickHouseException(errCode, rethrow);
        }
    }

    Future<HelloResponse> hello(String name, long reversion, String db, String user, String password) {
        Validate.ensure(channel.isActive());
        HelloRequest request = new HelloRequest(name, reversion, db, user, password);
        sendRequest(request);
        return CompletableFuture
                .supplyAsync(() -> recvResponse(HelloResponse.class, false))
                .whenComplete((response, throwable) -> {
                    boolean authenticated = throwable == null;
                    if (authenticated) {
                        setServerCtx(channel, serverContext(response, cfg));
                        changeState(SessionState.CONNECTED, SessionState.IDLE);
                    }
                });
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

    @SuppressWarnings("unchecked")
    <T extends Response> T recvResponse(Class<T> clz, boolean skipIfNotMatch) {
        while (true) {
            try {
                Response response = responseQueue.take();
                if (clz.isAssignableFrom(response.getClass())) {
                    return (T) response;
                }
                if (skipIfNotMatch) {
                    log.debug("expect {}, skip response: {}", clz.getSimpleName(), response.type());
                } else {
                    throw new ClickHouseException(
                            ClickHouseErrCode.UNEXPECTED_PACKET_FROM_SERVER.code(), response.type().toString());
                }
            } catch (InterruptedException rethrow) {
                throw new ClickHouseClientException(rethrow);
            }
        }
    }

    @Nullable
    <T extends Response, U extends Response> Response recvResponse(Class<T> clz,
                                                                   Class<U> clz2,
                                                                   Duration timeout,
                                                                   boolean nullIfTimeout,
                                                                   boolean skipIfNotMatch) {
        while (true) {
            try {
                Response response = responseQueue.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
                if (response == null && nullIfTimeout)
                    return null;
                if (response == null)
                    continue;
                if (clz.isAssignableFrom(response.getClass()) || clz2.isAssignableFrom(response.getClass()))
                    return response;
                if (skipIfNotMatch) {
                    log.debug("expect {} or {}, skip response: {}", clz.getSimpleName(), clz2.getSimpleName(), response.type());
                } else {
                    throw new ClickHouseException(
                            ClickHouseErrCode.UNEXPECTED_PACKET_FROM_SERVER.code(), response.type().toString());
                }
            } catch (InterruptedException rethrow) {
                throw new ClickHouseClientException(rethrow);
            }
        }
    }

    void checkState(SessionState expected) {
        Validate.ensure(stateAttr(channel).get() == expected,
                String.format(Locale.ROOT, "expected state [%s], but got [%s]",
                        expected, stateAttr(channel).get()));
    }

    void changeState(SessionState from, SessionState target) {
        Validate.ensure(stateAttr(channel).compareAndSet(from, target),
                String.format(Locale.ROOT,
                        "failed change state from [%s] to [%s], unexpected current state [%s]",
                        from, target, stateAttr(channel).get()));
        if (from != target)
            log.debug("channel[{}] change state from [{}] to [{}]", channel.id(), from, target);
    }

    void changeState(SessionState from1, SessionState from2, SessionState target) {
        SessionState currentState = stateAttr(channel).get();
        Validate.ensure(stateAttr(channel).compareAndSet(from1, target)
                        || stateAttr(channel).compareAndSet(from2, target),
                String.format(Locale.ROOT,
                        "failed change state from [%s] or [%s] to [%s], unexpected current state [%s]",
                        from1, from2, target, currentState));
        if (currentState != target)
            log.debug("channel[{}] change state from [{}] to [{}]", channel.id(), currentState, target);
    }

    static String nextId() {
        // return "ClickHouse-Native-JDBC-" + System.nanoTime();
        return UUID.randomUUID().toString();
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
        String clientName = "ClickHouse client";
        return new NativeContext.ClientContext(initialAddress, localHostName, clientName);
    }

    static NativeContext.ServerContext serverContext(HelloResponse response, ClickHouseConfig configure) {
        ZoneId timeZone = ZoneId.of(response.serverTimeZone());
        return new NativeContext.ServerContext(
                response.majorVersion(), response.minorVersion(), response.reversion(),
                configure, timeZone, response.serverDisplayName());
    }
}
