package com.github.housepower.jdbc.misc;

import java.sql.SQLException;

public class Validate {

    public static void isTrue(boolean expression) throws SQLException {
        isTrue(expression, null);
    }


    public static void isTrue(boolean expression, String message) throws SQLException {
        if (!expression) {
            throw new SQLException(message);
        }
    }
}
