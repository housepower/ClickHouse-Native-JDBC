package com.github.housepower.jdbc.connect;

import com.github.housepower.jdbc.protocol.QueryRequest.ClientInfo;
import com.github.housepower.jdbc.settings.ClickHouseConfig;

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
        private final ClickHouseConfig configure;

        public ServerInfo(ClickHouseConfig configure, long reversion, TimeZone timeZone, String displayName) {
            this.configure = configure;
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

        public ClickHouseConfig getConfigure() {
            return configure;
        }
    }
}
