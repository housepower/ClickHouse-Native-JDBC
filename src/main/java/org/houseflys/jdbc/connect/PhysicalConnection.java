package org.houseflys.jdbc.connect;

import org.houseflys.jdbc.protocol.RequestOrResponse;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.settings.ClickHouseConfig;
import org.houseflys.jdbc.settings.ClickHouseDefines;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.SQLException;

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

    public RequestOrResponse sendRequest(RequestOrResponse request) throws SQLException {
        return sendRequest(request, 0);
    }

    public RequestOrResponse sendRequest(RequestOrResponse request, int timeout) throws SQLException {
        try {
            socket.setSoTimeout(timeout);
            request.writeTo(serializer);
            serializer.flushToTarget(true);
            return RequestOrResponse.readFrom(request.type(), deserializer);
        } catch (IOException ex) {
            throw new SQLException(ex.getMessage(), ex);
        }
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

    public SocketAddress address() {
        return address;
    }

    public static PhysicalConnection openPhysicalConnection(ClickHouseConfig configure) throws SQLException {
        try {
            SocketAddress endpoint = new InetSocketAddress(configure.address(), configure.port());

            Socket socket = new Socket();
            socket.setTcpNoDelay(true);
            socket.setSendBufferSize(ClickHouseDefines.DBMS_DEFAULT_BUFFER_SIZE.intValue());
            socket.setReceiveBufferSize(ClickHouseDefines.DBMS_DEFAULT_BUFFER_SIZE.intValue());
            socket.connect(endpoint, configure.connectTimeout());

            return new PhysicalConnection(socket, new BinarySerializer(socket), new BinaryDeserializer(socket));
        } catch (IOException ex) {
            throw new SQLException(ex.getMessage(), ex);
        }
    }
}
