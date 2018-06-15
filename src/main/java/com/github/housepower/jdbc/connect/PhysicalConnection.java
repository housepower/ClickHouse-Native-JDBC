package com.github.housepower.jdbc.connect;

import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.protocol.*;
import com.github.housepower.jdbc.settings.ClickHouseDefines;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;
import com.github.housepower.jdbc.settings.ClickHouseConfig;
import com.github.housepower.jdbc.settings.SettingKey;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PhysicalConnection {
    private final Socket socket;
    private final SocketAddress address;
    private final BinarySerializer serializer;
    private final BinaryDeserializer deserializer;

    public PhysicalConnection(Socket socket, BinarySerializer serializer, BinaryDeserializer deserializer) {
        this.socket = socket;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.address = socket.getLocalSocketAddress();
    }

    public boolean ping(int soTimeout, PhysicalInfo.ServerInfo info) {
        try {
            sendRequest(new PingRequest());
            for (; ; ) {
                RequestOrResponse response = receiveResponse(soTimeout, info);
                Validate.isTrue(response instanceof ProgressResponse || response instanceof PongResponse,
                    "Expect Pong Response.");

                if (response instanceof PongResponse)
                    return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public void sendData(Block data) throws SQLException {
        sendRequest(new DataRequest("", data));
    }

    public void sendQuery(String query, QueryRequest.ClientInfo info, Map<SettingKey, Object> settings) throws SQLException {
        sendQuery(UUID.randomUUID().toString(), QueryRequest.COMPLETE_STAGE, info, query, settings);
    }

    public void sendHello(String client, long reversion, String db, String user, String password) throws SQLException {
        sendRequest(new HelloRequest(client, reversion, db, user, password));
    }

    public Block receiveSampleBlock(int soTimeout, PhysicalInfo.ServerInfo info) throws SQLException {
        while (true) {
            RequestOrResponse response = receiveResponse(soTimeout, info);
            if (response instanceof DataResponse) {
                return ((DataResponse) response).block();
            }
        }
    }

    public HelloResponse receiveHello(int soTimeout, PhysicalInfo.ServerInfo info) throws SQLException {
        RequestOrResponse response = receiveResponse(soTimeout, info);
        Validate.isTrue(response instanceof HelloResponse, "Expect Hello Response.");
        return (HelloResponse) response;
    }

    public EOFStreamResponse receiveEndOfStream(int soTimeout, PhysicalInfo.ServerInfo info) throws SQLException {
        RequestOrResponse response = receiveResponse(soTimeout, info);
        Validate.isTrue(response instanceof EOFStreamResponse, "Expect EOFStream Response.");
        return (EOFStreamResponse) response;
    }

    public RequestOrResponse receiveResponse(int soTimeout, PhysicalInfo.ServerInfo info) throws SQLException {
        try {
            socket.setSoTimeout(soTimeout);
            return RequestOrResponse.readFrom(deserializer, info);
        } catch (IOException ex) {
            throw new SQLException(ex.getMessage(), ex);
        }
    }

    public SocketAddress address() {
        return address;
    }

    public void disPhysicalConnection() throws SQLException {
        try {
            if (!socket.isClosed()) {
                serializer.flushToTarget(true);
                socket.close();
            }
        } catch (IOException ex) {
            throw new SQLException(ex.getMessage(), ex);
        }
    }

    private void sendQuery(String id, int stage, QueryRequest.ClientInfo info, String query,
        Map<SettingKey, Object> settings) throws SQLException {
        sendRequest(new QueryRequest(id, info, stage, true, query, settings));
    }

    private void sendRequest(RequestOrResponse request) throws SQLException {
        try {
            request.writeTo(serializer);
            serializer.flushToTarget(true);
        } catch (IOException ex) {
            throw new SQLException(ex.getMessage(), ex);
        }
    }

    public static PhysicalConnection openPhysicalConnection(ClickHouseConfig configure) throws SQLException {
        try {
            SocketAddress endpoint = new InetSocketAddress(configure.address(), configure.port());

            Socket socket = new Socket();
            socket.setTcpNoDelay(true);
            socket.setSendBufferSize(ClickHouseDefines.DEFAULT_BUFFER_SIZE);
            socket.setReceiveBufferSize(ClickHouseDefines.DEFAULT_BUFFER_SIZE);
            socket.connect(endpoint, configure.connectTimeout());

            return new PhysicalConnection(socket, new BinarySerializer(socket), new BinaryDeserializer(socket));
        } catch (IOException ex) {
            throw new SQLException(ex.getMessage(), ex);
        }
    }
}
