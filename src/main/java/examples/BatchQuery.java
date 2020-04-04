package examples;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 */
public class BatchQuery {

    public static void main(String[] args) throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000");

        Statement stmt = connection.createStatement();
        stmt.executeQuery("drop table if exists test_jdbc_example");
        stmt.executeQuery("create table test_jdbc_example(day Date, name String, age UInt8) Engine=Log");

        PreparedStatement pstmt = connection.prepareStatement("INSERT INTO test_jdbc_example VALUES(?, ?, ?)");

        for (int i = 1; i <= 200; i++) {
            pstmt.setDate(1, new Date(System.currentTimeMillis()));
            if(i%2==0)
            	pstmt.setString(2, "Zhang San" + i);
            else
            	pstmt.setString(2, "Zhang San");
            pstmt.setByte(3, (byte)((i%4)*15));
            System.out.println(pstmt);
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        
        if(pstmt!=null)
        	pstmt.close();
        
        pstmt=connection.prepareStatement("select count(*) from test_jdbc_example where age>? and age<=?");
        pstmt.setByte(1, (byte)10);
        pstmt.setByte(2, (byte)30);
		printCount(pstmt);
        if(pstmt!=null)
        	pstmt.close();
        
        pstmt=connection.prepareStatement("select count(*) from test_jdbc_example where name=?");
        pstmt.setString(1, "Zhang San");
        printCount(pstmt);
        
        stmt.executeQuery("drop table test_jdbc_example");
    }

	public static void printCount(PreparedStatement pstmt) throws SQLException {
		ResultSet rs=pstmt.executeQuery();
        System.out.println(pstmt);
        if(rs.next())
        	System.out.println(rs.getInt(1));
        
        if(rs!=null)
        	rs.close();
	}
}
