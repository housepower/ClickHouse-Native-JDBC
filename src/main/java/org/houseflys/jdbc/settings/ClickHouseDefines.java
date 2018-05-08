package org.houseflys.jdbc.settings;

public enum ClickHouseDefines {
    DBMS_NAME("name", "ClickHouse", "Database Product Name."),
    DBMS_VERSION_MAJOR("major_version", 1, ""),
    DBMS_VERSION_MINOR("minor_version", 1, ""),
    DBMS_CLIENT_REVERSION("client_reversion", 54380, ""),
    DBMS_DEFAULT_DATABASE("default_database", "default", ""),
    DBMS_DEFAULT_BUFFER_SIZE("default_buffer_size", 1048576, ""),
    DBMS_MIN_REVISION_WITH_SERVER_TIMEZONE("", 54058, ""),
    DBMS_MIN_REVISION_WITH_SERVER_DISPLAY_NAME("", 54372, "");

    private final Class clazz;
    private final Object value;
    private final String description;

    <T> ClickHouseDefines(String key, T value, String description) {
        this.value = value;
        this.clazz = value.getClass();
        this.description = description;
    }

    public int intValue() {
        return numberValue().intValue();
    }

    public long longValue() {
        return numberValue().longValue();
    }

    public String stringValue() {
        if (String.class.isAssignableFrom(clazz)) {
            return String.valueOf(value);
        }
        throw new RuntimeException();
    }

    private Number numberValue() {
        if (Number.class.isAssignableFrom(clazz)) {
            return (Number) value;
        }
        throw new RuntimeException();
    }
}
