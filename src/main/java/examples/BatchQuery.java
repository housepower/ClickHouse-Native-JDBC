package examples;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 */
public class BatchQuery {

    public static void main(String[] args) throws Exception {
        Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000");

        Statement stmt = connection.createStatement();
        stmt.executeQuery("drop table if exists test_jdbc_example");
        stmt.executeQuery("create table test_jdbc_example(day Date, name String, age UInt8) Engine=Log");

        PreparedStatement pstmt = connection.prepareStatement("INSERT INTO test_jdbc_example VALUES(?, ?, ?)");

        for (int i = 0; i < 200; i++) {
            pstmt.setDate(1, new Date(System.currentTimeMillis()));
            pstmt.setString(2, "Zhang San" + i);
            pstmt.setByte(3, (byte)i);
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        stmt.executeQuery("drop table test_jdbc_example");
    }
}
