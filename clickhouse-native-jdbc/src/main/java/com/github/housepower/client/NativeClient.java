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

import com.github.housepower.buffer.SocketBuffedReader;
import com.github.housepower.buffer.SocketBuffedWriter;
import com.github.housepower.client.ssl.SSLContextBuilder;
import com.github.housepower.data.Block;
import com.github.housepower.misc.Validate;
import com.github.housepower.protocol.*;
import com.github.housepower.serde.BinaryDeserializer;
import com.github.housepower.serde.BinarySerializer;
import com.github.housepower.settings.ClickHouseConfig;
import com.github.housepower.settings.ClickHouseDefines;
import com.github.housepower.settings.SettingKey;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.stream.QueryResult;
import com.github.housepower.stream.ClickHouseQueryResult;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

// TODO throw ClickHouseException instead of SQLException
public class NativeClient {

    private static final Logger LOG = LoggerFactory.getLogger(NativeClient.class);

    public static NativeClient connect(ClickHouseConfig config) throws SQLException {
        return connect(config.host(), config.port(), config);
    }

    // TODO: Support proxy
    // TODO: Move socket initialisation to separate factory (default & ssl)
    public static NativeClient connect(String host, int port, ClickHouseConfig config) throws SQLException {
        try {
            SocketAddress endpoint = new InetSocketAddress(host, port);
            Socket socket;

            boolean useSSL = config.ssl();
            if (useSSL) {
                LOG.debug("Client works in SSL mode!");
                SSLContext context = new SSLContextBuilder(config).getSSLContext();
                SSLSocketFactory factory = context.getSocketFactory();
                socket = (SSLSocket) factory.createSocket();
            } else {
                socket = new Socket();
            }
            socket.setTcpNoDelay(true);
            socket.setSendBufferSize(ClickHouseDefines.SOCKET_SEND_BUFFER_BYTES);
            socket.setReceiveBufferSize(ClickHouseDefines.SOCKET_RECV_BUFFER_BYTES);
            socket.setKeepAlive(config.tcpKeepAlive());
            socket.connect(endpoint, (int) config.connectTimeout().toMillis());

            if (useSSL) ((SSLSocket) socket).startHandshake();

            return new NativeClient(socket);
        } catch (IOException |
                 NoSuchAlgorithmException |
                 KeyStoreException |
                 CertificateException |
                 UnrecoverableKeyException |
                 KeyManagementException ex) {
            throw new SQLException(ex.getMessage(), ex);
        }
    }

    private final Socket socket;
    private final SocketAddress address;
    private final boolean compression;
    private final BinarySerializer serializer;
    private final BinaryDeserializer deserializer;

    private NativeClient(Socket socket) throws IOException {
        this.socket = socket;
        this.address = socket.getLocalSocketAddress();
        this.compression = ClickHouseDefines.COMPRESSION;

        this.serializer = new BinarySerializer(new SocketBuffedWriter(socket), compression);
        this.deserializer = new BinaryDeserializer(new SocketBuffedReader(socket), compression);
    }

    public SocketAddress address() {
        return address;
    }

    public boolean ping(Duration soTimeout, NativeContext.ServerContext info) {
        try {
            sendRequest(PingRequest.INSTANCE);
            while (true) {
                Response response = receiveResponse(soTimeout, info);

                if (response instanceof PongResponse)
                    return true;

                // TODO there are some previous response we haven't consumed
                LOG.debug("expect pong, skip response: {}", response.type());
            }
        } catch (SQLException e) {
            LOG.warn(e.getMessage());
            return false;
        }
    }

    public Block receiveSampleBlock(Duration soTimeout, NativeContext.ServerContext info) throws SQLException {
        while (true) {
            Response response = receiveResponse(soTimeout, info);
            if (response instanceof DataResponse) {
                return ((DataResponse) response).block();
            }
            // TODO there are some previous response we haven't consumed
            LOG.debug("expect sample block, skip response: {}", response.type());
        }
    }

    public void sendHello(String client, long reversion, String db, String user, String password) throws SQLException {
        sendRequest(new HelloRequest(client, reversion, db, user, password));
    }

    public void sendQuery(String query, NativeContext.ClientContext info, Map<SettingKey, Serializable> settings) throws SQLException {
        sendQuery((String) settings.getOrDefault(SettingKey.query_id, UUID.randomUUID().toString()),
                QueryRequest.STAGE_COMPLETE, info, query, settings);
    }

    public void sendData(Block data) throws SQLException {
        sendRequest(new DataRequest("", data));
    }

    public HelloResponse receiveHello(Duration soTimeout, NativeContext.ServerContext info) throws SQLException {
        Response response = receiveResponse(soTimeout, info);
        Validate.isTrue(response instanceof HelloResponse, "Expect Hello Response.");
        return (HelloResponse) response;
    }

    public EOFStreamResponse receiveEndOfStream(Duration soTimeout, NativeContext.ServerContext info) throws SQLException {
        Response response = receiveResponse(soTimeout, info);
        Validate.isTrue(response instanceof EOFStreamResponse, "Expect EOFStream Response.");
        return (EOFStreamResponse) response;
    }

    public QueryResult receiveQuery(Duration soTimeout, NativeContext.ServerContext info) {
        return new ClickHouseQueryResult(() -> receiveResponse(soTimeout, info));
    }

    public void silentDisconnect() {
        try {
            disconnect();
        } catch (Throwable th) {
            LOG.debug("disconnect throw exception.", th);
        }
    }

    public void disconnect() throws SQLException {
        try {
            if (socket.isClosed()) {
                LOG.info("socket already closed, ignore");
                return;
            }
            LOG.trace("flush and close socket");
            serializer.flushToTarget(true);
            socket.close();
        } catch (IOException ex) {
            throw new SQLException(ex.getMessage(), ex);
        }
    }

    private void sendQuery(String id, int stage, NativeContext.ClientContext info, String query,
                           Map<SettingKey, Serializable> settings) throws SQLException {
        sendRequest(new QueryRequest(id, info, stage, compression, query, settings));
    }

    private void sendRequest(Request request) throws SQLException {
        try {
            LOG.trace("send request: {}", request.type());
            request.writeTo(serializer);
            serializer.flushToTarget(true);
        } catch (IOException ex) {
            throw new SQLException(ex.getMessage(), ex);
        }
    }

    private Response receiveResponse(Duration soTimeout, NativeContext.ServerContext info) throws SQLException {
        try {
            socket.setSoTimeout(((int) soTimeout.toMillis()));
            Response response = Response.readFrom(deserializer, info);
            LOG.trace("recv response: {}", response.type());
            return response;
        } catch (IOException ex) {
            throw new SQLException(ex.getMessage(), ex);
        }
    }
}
