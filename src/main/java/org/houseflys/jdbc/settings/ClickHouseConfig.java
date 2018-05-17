package org.houseflys.jdbc.settings;

public class ClickHouseConfig {

    private final int port;

    private final String address;

    private final String database;

    private final Settings settings;


    public ClickHouseConfig(String address, int port, String database, Settings settings) {
        this.port = port;
        this.address = address;
        this.database = database;
        this.settings = settings;
    }

    public int port() {
        return this.port;
    }

    public String address() {
        return this.address;
    }

    public String database() {
        Setting databaseSetting = settings.getSetting(SettingsKey.DATABASE);
        return databaseSetting.value() == null ? database : String.valueOf(databaseSetting.value());
    }

    public String username() {
        Setting userName = settings.getSetting(SettingsKey.USER);
        return userName.value() == null ? "default" : String.valueOf(userName.value());
    }

    public String password() {
        Setting userPassword = settings.getSetting(SettingsKey.PASSWORD);
        return userPassword.value() == null ? "" : String.valueOf(userPassword.value());
    }

    public int connectTimeout() {
        Setting connectTimeout = settings.getSetting(SettingsKey.CONNECT_TIMEOUT);
        return connectTimeout.value() == null ? 0 : Integer.valueOf(String.valueOf(connectTimeout.value()));
    }
}
