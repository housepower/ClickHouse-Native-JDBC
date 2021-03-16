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

import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.protocol.grpc.ClickHouseGrpc;
import com.github.housepower.protocol.grpc.QueryInfo;
import com.github.housepower.protocol.grpc.Result;
import com.github.housepower.settings.ClickHouseConfig;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Locale;
import java.util.UUID;

public class GrpcConnection implements IConnection {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcConnection.class);

    public static GrpcConnection create(ClickHouseConfig cfg) {
        GrpcConnection grpcConnection = new GrpcConnection(cfg);
        grpcConnection.initChannel();
        return grpcConnection;
    }

    private volatile ManagedChannel channel;
    private volatile ClickHouseGrpc.ClickHouseBlockingStub blockingStub;
    private volatile ClickHouseGrpc.ClickHouseFutureStub futureStub;

    private volatile String sessionId;
    private volatile ClickHouseConfig cfg;

    private volatile QueryInfo baseQueryInfo;

    public GrpcConnection(ClickHouseConfig cfg) {
        this.cfg = cfg;
    }

    public void initChannel() {
        this.channel = ManagedChannelBuilder.forAddress(cfg.host(), cfg.port()).usePlaintext().build();
        this.blockingStub = ClickHouseGrpc.newBlockingStub(channel);
        this.futureStub = ClickHouseGrpc.newFutureStub(channel);
        this.sessionId = newSessionId();
        this.baseQueryInfo = QueryInfo.newBuilder()
                .setUserName(cfg.user())
                .setPassword(cfg.password())
                .buildPartial();
    }

    public String sessionId() {
        return sessionId;
    }

    @Override
    public ClickHouseConfig cfg() {
        return cfg;
    }

    @Override
    public void updateCfg(ClickHouseConfig cfg) {
        this.cfg = cfg;
    }

    public ClickHouseGrpc.ClickHouseBlockingStub blockingStub() {
        return this.blockingStub;
    }

    public ClickHouseGrpc.ClickHouseFutureStub futureStub() {
        return this.futureStub;
    }

    public Result syncQuery(String sql) {
        LOG.info("Execute ClickHouse SQL:\n{}", sql);
        QueryInfo queryInfo = QueryInfo.newBuilder(baseQueryInfo)
                .setQuery(sql)
                .setQueryId(UUID.randomUUID().toString())
                .setOutputFormat("JSON")
                .build();
        return blockingStub.executeQuery(queryInfo);
    }

    public Result syncInsert(String database, String table,
                             String inputFormat,
                             byte[] data) {
        QueryInfo queryInfo = QueryInfo.newBuilder(baseQueryInfo)
                .setQuery(String.format(Locale.ROOT,
                        "INSERT INTO `%s`.`%s` FORMAT %s",
                        database, table, inputFormat))
                .setQueryId(UUID.randomUUID().toString())
                .setInputDataBytes(ByteString.copyFrom(data))
                .setOutputFormat("JSON")
                .build();
        return blockingStub.executeQuery(queryInfo);
    }

    @Override
    public String newQueryId() {
        return "GRPC-JDBC-Query-" + UUID.randomUUID();
    }

    public String newSessionId() {
        return "GRPC-JDBC-Session-" + UUID.randomUUID();
    }
}
