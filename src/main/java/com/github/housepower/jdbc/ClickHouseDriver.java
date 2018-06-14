package com.github.housepower.jdbc;

import java.sql.DriverManager;
import java.sql.SQLException;

public class ClickHouseDriver extends NonRegisterDriver {
    static {
        try {
            DriverManager.registerDriver(new ClickHouseDriver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
