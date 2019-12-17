package com.github.housepower.jdbc.settings;

public class ClickHouseDefines {
    public static final String NAME = "ClickHouse";
    public static final String DEFAULT_DATABASE = "default";

    public static final Integer MAJOR_VERSION = 1;
    public static final Integer MINOR_VERSION = 1;
    public static final Integer CLIENT_REVERSION = 54380;
    public static final Integer DBMS_MIN_REVISION_WITH_SERVER_TIMEZONE = 54058;
    public static final Integer DBMS_MIN_REVISION_WITH_SERVER_DISPLAY_NAME = 54372;

    public static final int MAX_BLOCK_SIZE = 1048576 * 10;
    public static int SOCKET_BUFFER_SIZE = 1048576;
    public static int BUFFER_ROWS = 102400;
}
