package org.houseflys.jdbc.connect;

import static org.houseflys.jdbc.settings.ClickHouseDefines.DBMS_CLIENT_REVERSION;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.protocol.HelloRequest;
import org.houseflys.jdbc.protocol.HelloResponse;
import org.houseflys.jdbc.protocol.RequestOrResponse;
import org.houseflys.jdbc.settings.ClickHouseConfig;

public class FailoverConnection {
    private final AtomicReference<PhysicalConnection> atomicPhysical = new AtomicReference<PhysicalConnection>();

    public FailoverConnection(ServerInfo info, PhysicalConnection physical) {
        atomicPhysical.compareAndSet(null, physical);
    }

    public static FailoverConnection openFailoverConnection(ClickHouseConfig configure) throws SQLException {
        PhysicalConnection physicalConnection = PhysicalConnection.openPhysicalConnection(configure);
        RequestOrResponse response = physicalConnection.sendRequest(new HelloRequest(
            "client", DBMS_CLIENT_REVERSION.longValue(),
            configure.database(), configure.username(), configure.password())
        );

        Validate.isTrue(response instanceof HelloResponse, "");
        long reversion = ((HelloResponse) response).reversion();
        String serverName = ((HelloResponse) response).serverName();
        String displayName = ((HelloResponse) response).serverDisplayName();
        ServerInfo info = new ServerInfo(reversion, serverName, displayName);

        return new FailoverConnection(info, physicalConnection);
    }

    public static class ServerInfo {
        private final long reversion;
        private final String serverName;
        private final String displayName;

        public ServerInfo(long reversion, String serverName, String displayName) {
            this.reversion = reversion;
            this.serverName = serverName;
            this.displayName = displayName;
        }

        public long reversion() {
            return reversion;
        }

        public String serverName() {
            return serverName;
        }

        public String displayName() {
            return displayName;
        }
    }
}
