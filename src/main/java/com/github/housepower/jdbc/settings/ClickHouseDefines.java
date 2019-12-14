package com.github.housepower.jdbc.settings;

public class ClickHouseDefines {
    public static final String NAME = "ClickHouse";
    public static final String DEFAULT_DATABASE = "default";

    public static final Integer MAJOR_VERSION = 1;
    public static final Integer MINOR_VERSION = 1;
    public static final Integer CLIENT_REVERSION = 54380;
    public static final Integer DBMS_MIN_REVISION_WITH_SERVER_TIMEZONE = 54058;
    public static final Integer DBMS_MIN_REVISION_WITH_SERVER_DISPLAY_NAME = 54372;

    private static int socketBufferSize = 1048576;
    private static int bufferRows = 102400;

    public static int getSocketBufferSize() {
        return socketBufferSize;
    }

    public static void setSocketBufferSize(int socketBufferSize_) {
        socketBufferSize = socketBufferSize;
    }

    public static int getBufferRows() {
        return bufferRows;
    }

    public static void setBufferRows(int bufferRows_) {
        bufferRows = bufferRows_;
    }
}
