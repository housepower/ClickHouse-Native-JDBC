package org.houseflys.jdbc.stream;

import org.houseflys.jdbc.data.Block;

import java.sql.SQLException;

public interface InputFormat {
    Block next(Block header, int maxRows) throws SQLException;
}
