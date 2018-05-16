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
    private final InetSocketAddress endpoint;
    private final AtomicBoolean connected = new AtomicBoolean(false);

    private Socket socket;
    private BinarySerializer serializer;
    private BinaryDeserializer deserializer;

    public PhysicalConnection(ClickHouseConfig configure) throws Exception {
        //        this.timeout = configure.intValue("connect_timeout");
        this.endpoint = new InetSocketAddress(configure.address(), configure.port());
    }

    public void connect() throws IOException {
        if (connected.compareAndSet(false, true)) {
            try {
                socket = new Socket();
                socket.setTcpNoDelay(true);
                socket.connect(endpoint/*, timeout*/);
                socket.setSendBufferSize(ClickHouseDefines.DBMS_DEFAULT_BUFFER_SIZE.intValue());
                socket.setReceiveBufferSize(ClickHouseDefines.DBMS_DEFAULT_BUFFER_SIZE.intValue());
                /// TODO: configure read timeout
//                socket.setSoTimeout(timeout);

                serializer = new BinarySerializer(socket);
                deserializer = new BinaryDeserializer(socket);
            } catch (Exception ex) {
                connected.set(false);
            }
        }
    }

    public RequestOrResponse sendRequest(RequestOrResponse request) throws IOException, SQLException {
        request.writeTo(serializer);
        serializer.flushToTarget(true);
        return RequestOrResponse.readFrom(request.type(), deserializer);
    }

    public void close() throws IOException {
        if (!socket.isClosed() && connected.compareAndSet(false, true)) {
            serializer.flushToTarget(true);
            socket.close();
        }
    }
}
