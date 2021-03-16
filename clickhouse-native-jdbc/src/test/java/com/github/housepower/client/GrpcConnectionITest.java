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

import com.github.housepower.jdbc.AbstractITest;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.protocol.grpc.ClickHouseGrpc;
import com.github.housepower.protocol.grpc.QueryInfo;
import com.github.housepower.protocol.grpc.Result;
import com.github.housepower.settings.ClickHouseConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GrpcConnectionITest extends AbstractITest {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcConnectionITest.class);

    protected static ClickHouseConfig cfg;
    protected static GrpcConnection grpcConnection;
    protected static QueryInfo baseQueryInfo;
    protected static ClickHouseGrpc.ClickHouseBlockingStub blockingStub;
    protected static ClickHouseGrpc.ClickHouseFutureStub futureStub;

    @BeforeAll
    public static void beforeAll() {
        cfg = ClickHouseConfig.Builder.builder()
                .host(CK_HOST)
                .port(CK_GRPC_PORT)
                .build();
        grpcConnection = GrpcConnection.create(cfg);
        baseQueryInfo = QueryInfo.newBuilder()
                .setUserName(CLICKHOUSE_USER)
                .setPassword(CLICKHOUSE_PASSWORD)
                .buildPartial();
        blockingStub = grpcConnection.blockingStub();
        futureStub = grpcConnection.futureStub();
    }

    @Test
    public void testQuery() {
        Result result = grpcConnection.syncQuery("select now()");
        LOG.info("execute: select now()");
        LOG.info(result.toString());
    }

    @Test
    public void testInsert() {

    }
}
