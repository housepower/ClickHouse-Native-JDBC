package examples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 */
public class ExecuteQuery {

  public static void main(String[] args) throws Exception {
    Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
    Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000");

    Statement stmt = connection.createStatement();
    // drop table
    stmt.executeQuery("drop table if exists test_jdbc_example");
    // create table
    stmt.executeQuery("create table test_jdbc_example(day default toDate( toDateTime(timestamp) ), timestamp UInt32, name String, impressions UInt32) Engine=MergeTree(day, (timestamp, name), 8192)");
    // add column `costs`
    stmt.executeQuery("alter table test_jdbc_example add column costs Float32");
    // drop the table
    stmt.executeQuery("drop table test_jdbc_example");
  }
}
