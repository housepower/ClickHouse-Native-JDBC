package examples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 */
public class ExecuteQuery {

  public static void main(String[] args) throws Exception {
    Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000");

    Statement stmt = connection.createStatement();
    stmt.executeQuery("drop table if exists test_jdbc_example");
    stmt.executeQuery("create table test_jdbc_example(day default toDate( toDateTime(timestamp) ), timestamp UInt32, name String, impressions UInt32) Engine=MergeTree(day, (timestamp, name), 8192)");
    stmt.executeQuery("alter table test_jdbc_example add column costs Float32");
    stmt.executeQuery("drop table test_jdbc_example");
  }
}
