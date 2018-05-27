package org.houseflys.jdbc.connect;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.houseflys.jdbc.settings.ClickHouseConfig;
import org.houseflys.jdbc.settings.ClickHouseDefines;
import org.houseflys.jdbc.protocol.RequestOrResponse;
import org.houseflys.jdbc.buffer.SocketBuffedReader;
import org.houseflys.jdbc.buffer.SocketBuffedWriter;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;

public class PhysicalConnection {

    //    private final int timeout;
    private final int timeout;
    private final InetSocketAddress endpoint;
    private final AtomicBoolean connected = new AtomicBoolean(false);

    private Socket socket;
    private BinarySerializer serializer;
    private BinaryDeserializer deserializer;
    private String hostAddress;
    private int localPort;

    public PhysicalConnection(ClickHouseConfig configure) throws Exception {
        timeout = configure.connectTimeout();
        this.endpoint = new InetSocketAddress(configure.address(), configure.port());
    }

    public void connect() throws IOException {
        if (connected.compareAndSet(false, true)) {
            try {
                socket = new Socket();
                socket.setTcpNoDelay(true);
                socket.setSendBufferSize(ClickHouseDefines.DBMS_DEFAULT_BUFFER_SIZE.intValue());
                socket.setReceiveBufferSize(ClickHouseDefines.DBMS_DEFAULT_BUFFER_SIZE.intValue());

                socket.connect(endpoint, timeout);
                serializer = new BinarySerializer(socket);
                deserializer = new BinaryDeserializer(socket);

                localPort = socket.getLocalPort();
                hostAddress = socket.getLocalAddress().getHostAddress();
            } catch (Exception ex) {
                connected.set(false);
                throw new RuntimeException(ex);
            }
        }
    }

    public RequestOrResponse sendRequest(RequestOrResponse request) throws IOException, SQLException {
        request.writeTo(serializer);
        serializer.flushToTarget(true);
        return RequestOrResponse.readFrom(request.type(), deserializer);
    }

    public RequestOrResponse sendRequest(RequestOrResponse request, int timeout) throws IOException, SQLException {
        socket.setSoTimeout(timeout);
        return sendRequest(request);
    }

    public void close() throws SQLException {
        if (!socket.isClosed() && connected.compareAndSet(false, true)) {
            try {
                serializer.flushToTarget(true);
                socket.close();
            } catch (Exception ex) {
                throw new SQLException(ex.getMessage(), ex);
            }
        }
    }

    public int localPort() {
        return localPort;
    }

    public String localAddress() {
        return hostAddress;
    }
}
