package com.github.housepower.jdbc.stream;

import com.github.housepower.jdbc.data.Block;

import java.sql.SQLException;

public interface InputFormat {
    Block next(Block header, int maxRows) throws SQLException;
}
