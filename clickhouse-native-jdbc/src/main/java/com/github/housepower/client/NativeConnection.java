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
import com.github.housepower.exception.ExceptionUtil;
import com.github.housepower.exception.NotImplementedException;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.misc.Validate;
import com.github.housepower.netty.ChannelHelper;
import com.github.housepower.netty.ChannelState;
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

import static com.github.housepower.protocol.Response.ProtoType.*;

public class NativeConnection implements ChannelHelper, AutoCloseable {

    public static NativeConnection create(ClickHouseConfig cfg) {
        NativeConnection conn = new NativeConnection(NativeBootstrap.DEFAULT, cfg);
        conn.initChannel();
        return conn;
    }

    private static final Logger LOG = LoggerFactory.getLogger(NativeConnection.class);

    private final NativeBootstrap bootstrap;
    private volatile ClickHouseConfig cfg;
    private volatile Channel channel;
    private volatile NativeContext ctx;
    private volatile BlockingQueue<Response> responseQueue;

    public NativeConnection(NativeBootstrap bootstrap, ClickHouseConfig cfg) {
        this.bootstrap = bootstrap;
        this.cfg = cfg;
    }

    public ClickHouseConfig cfg() {
        return cfg;
    }

    public void updateCfg(ClickHouseConfig cfg) {
        this.cfg = cfg;
    }

    public NativeContext ctx() {
        return ctx;
    }

    public void initChannel() {
        this.channel = bootstrap.connect(cfg.host(), cfg.port());
        Validate.ensure(channel.isActive() && channel.isRegistered());
        stateAttr(channel).set(ChannelState.INIT);

        this.responseQueue = newResponseQueue();
        setResponseQueue(channel, responseQueue);
        Validate.ensure(stateAttr(channel).compareAndSet(ChannelState.INIT, ChannelState.CONNECTED));

        NativeContext.ClientContext clientCtx = clientContext(channel);
        setClientCtx(channel, clientCtx);
        syncHello("ClickHouse-Native-JDBC", ClickHouseDefines.CLIENT_REVISION, cfg.database(), cfg.user(), cfg.password());
        NativeContext.ServerContext serverCtx = getServerCtx(channel);
        this.ctx = new NativeContext(clientCtx, serverCtx, this);
    }

    synchronized void checkOrRepairChannel() {
        if (!syncPing(Duration.ofMillis(1000))) {
            LOG.warn("current channel maybe broken, create new channel");
            silentClose();
            initChannel();
        }
    }

    @Override
    public void close() throws Exception {
        channel.close().sync();
    }

    public void silentClose() {
        try {
            close();
        } catch (Throwable th) {
            LOG.debug("Close NativeConnection throw exception, ignored", th);
        }
    }

    public Future<Boolean> ping() {
        Validate.ensure(channel.isActive());
        PingRequest request = PingRequest.INSTANCE;
        sendRequest(request);
        return CompletableFuture
                .supplyAsync(() -> recvResponse(RESPONSE_PONG, true))
                .handle((response, throwable) -> {
                    boolean active = throwable == null;
                    if (active && state() != ChannelState.IDLE)
                        changeState(ChannelState.WAITING_INSERT, ChannelState.IDLE);
                    return active;
                });
    }

    public boolean syncPing(Duration timeout) {
        try {
            if (timeout.isZero()) {
                // match so_timeout behavior
                return ping().get();
            }
            return ping().get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOG.debug("ping failed", e);
            return false;
        }
    }

    public Future<Block> sampleBlock(String sampleSql) {
        LOG.debug("sample sql: {}", sampleSql);
        checkOrRepairChannel();
        checkState(ChannelState.IDLE);
        QueryRequest request = new QueryRequest(
                nextId(),
                getClientCtx(channel),
                QueryRequest.STAGE_COMPLETE,
                false, // TODO support compress
                sampleSql,
                cfg.settings());
        sendRequest(request);

        return CompletableFuture
                .supplyAsync(() -> (DataResponse) recvResponse(RESPONSE_DATA, true))
                .thenApply(dataResponse -> {
                    changeState(ChannelState.IDLE, ChannelState.WAITING_INSERT);
                    return dataResponse.block();
                });
    }

    public Block syncSampleBlock(String sampleSql) {
        try {
            return sampleBlock(sampleSql).get();
        } catch (Exception rethrow) {
            LOG.error("sample block failed\n=== failed sql ===\n{}\n===", sampleSql, rethrow);
            int errCode = ClickHouseErrCode.UNKNOWN_ERROR.code();
            ClickHouseException ex = ExceptionUtil.recursiveFind(rethrow, ClickHouseException.class);
            if (ex != null)
                errCode = ex.errCode();
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
                        recvResponse(RESPONSE_DATA, RESPONSE_END_OF_STREAM, Duration.ofMillis(300), false, true)));
    }

    public QueryResult syncQuery(String querySql, Map<SettingKey, Object> settings) {
        try {
            return query(querySql, settings).get();
        } catch (Exception rethrow) {
            LOG.error("query failed\n=== failed sql ===\n{}\n===", querySql, rethrow);
            int errCode = ClickHouseErrCode.UNKNOWN_ERROR.code();
            ClickHouseException ex = ExceptionUtil.recursiveFind(rethrow, ClickHouseException.class);
            if (ex != null)
                errCode = ex.errCode();
            throw new ClickHouseException(errCode, rethrow);
        }
    }

    public Future<Void> cancel() {
        throw new NotImplementedException("cancel not implemented");
    }

    public Future<Void> store(Block block) {
        checkState(ChannelState.WAITING_INSERT);
        DataRequest request = new DataRequest("", block);
        sendRequest(request);
        sendRequest(DataRequest.EMPTY);
        return CompletableFuture
                .supplyAsync(() -> recvResponse(RESPONSE_END_OF_STREAM, false))
                .thenAccept(eos -> changeState(ChannelState.WAITING_INSERT, ChannelState.IDLE));
    }

    public void syncStore(Block block) {
        try {
            store(block).get();
        } catch (Exception rethrow) {
            LOG.error("store failed", rethrow);
            int errCode = ClickHouseErrCode.UNKNOWN_ERROR.code();
            ClickHouseException ex = ExceptionUtil.recursiveFind(rethrow, ClickHouseException.class);
            if (ex != null)
                errCode = ex.errCode();
            throw new ClickHouseException(errCode, rethrow);
        }
    }

    Future<HelloResponse> hello(String name, long reversion, String db, String user, String password) {
        Validate.ensure(channel.isActive());
        HelloRequest request = new HelloRequest(name, reversion, db, user, password);
        sendRequest(request);
        return CompletableFuture
                .supplyAsync(() -> (HelloResponse) recvResponse(RESPONSE_HELLO, false))
                .whenComplete((response, throwable) -> {
                    boolean authenticated = throwable == null;
                    if (authenticated) {
                        setServerCtx(channel, serverContext(response, cfg));
                        changeState(ChannelState.CONNECTED, ChannelState.IDLE);
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
                errCode = ex.errCode();
            throw new ClickHouseException(errCode, rethrow);
        }
    }

    void sendRequest(Request request) {
        channel.writeAndFlush(request);
    }

    @SuppressWarnings("unchecked")
    <T extends Response> T recvResponse(Response.ProtoType type, boolean skipIfNotMatch) {
        while (true) {
            try {
                Response response = responseQueue.take();
                if (response.type() == type) {
                    return (T) response;
                }
                if (response.type() == RESPONSE_EXCEPTION) {
                    throw ((ExceptionResponse) response).exception();
                }
                if (skipIfNotMatch) {
                    LOG.debug("expect {}, skip response: {}", type, response.type());
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
    Response recvResponse(Response.ProtoType type1,
                          Response.ProtoType type2,
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
                if (response.type() == type1 || response.type() == type2)
                    return response;
                if (response.type() == RESPONSE_EXCEPTION) {
                    throw ((ExceptionResponse) response).exception();
                }
                if (skipIfNotMatch) {
                    LOG.debug("expect {} or {}, skip response: {}", type1, type2, response.type());
                } else {
                    throw new ClickHouseException(
                            ClickHouseErrCode.UNEXPECTED_PACKET_FROM_SERVER.code(), response.type().toString());
                }
            } catch (InterruptedException rethrow) {
                throw new ClickHouseClientException(rethrow);
            }
        }
    }

    ChannelState state() {
        return stateAttr(channel).get();
    }

    void checkState(ChannelState expected) {
        Validate.ensure(state() == expected,
                String.format(Locale.ROOT, "expected state [%s], but got [%s]",
                        expected, stateAttr(channel).get()));
    }

    void changeState(ChannelState from, ChannelState target) {
        Validate.ensure(stateAttr(channel).compareAndSet(from, target),
                String.format(Locale.ROOT,
                        "failed change state from [%s] to [%s], unexpected current state [%s]",
                        from, target, state()));
        if (from != target)
            LOG.debug("channel[{}] change state from [{}] to [{}]", channel.id(), from, target);
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
