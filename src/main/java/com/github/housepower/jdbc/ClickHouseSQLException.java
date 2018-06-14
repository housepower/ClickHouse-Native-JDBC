package com.github.housepower.jdbc;

import java.sql.SQLException;

public class ClickHouseSQLException extends SQLException {

    private final int code;

    public ClickHouseSQLException(int code, String message) {
        this(code, message, null);
    }

    public ClickHouseSQLException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }


    public int getCode() {
        return code;
    }
}
