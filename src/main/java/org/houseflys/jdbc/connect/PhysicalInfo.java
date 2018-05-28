package org.houseflys.jdbc.connect;

import org.houseflys.jdbc.protocol.QueryRequest.ClientInfo;

import java.util.TimeZone;

public class PhysicalInfo {

    private final ClientInfo client;
    private final ServerInfo server;
    private final PhysicalConnection connection;

    public PhysicalInfo(ClientInfo client, ServerInfo server, PhysicalConnection connection) {
        this.client = client;
        this.server = server;
        this.connection = connection;
    }

    public ClientInfo client() {
        return client;
    }

    public ServerInfo server() {
        return server;
    }

    public PhysicalConnection connection() {
        return connection;
    }

    public static class ServerInfo {
        private final long reversion;
        private final TimeZone timeZone;
        private final String displayName;

        public ServerInfo(long reversion, TimeZone timeZone, String displayName) {
            this.reversion = reversion;
            this.timeZone = timeZone;
            this.displayName = displayName;
        }

        public long reversion() {
            return reversion;
        }

        public TimeZone timeZone() {
            return timeZone;
        }

        public String displayName() {
            return displayName;
        }
    }
}
