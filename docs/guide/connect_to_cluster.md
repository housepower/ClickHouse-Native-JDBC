## Connect to Cluster

### BalancedClickhouseDataSource

We can initial `BalancedClickhouseDataSource` with a jdbc url which contains multiple clickhouse instance addresses, 
and each time when call `#getConnection`, a health connection which connected to one of the instances will be given. 

Currently, we only support random algorithm for clickhouse instances selection.
  
The `BalancedClickhouseDataSource` can be shared in different threads.

- Example codes:

```java
DataSource singleDataSource = new BalancedClickhouseDataSource("jdbc:clickhouse://127.0.0.1:9000");

DataSource dualDataSource = new BalancedClickhouseDataSource("jdbc:clickhouse://127.0.0.1:9000,127.0.0.1:9000");

Connection conn1 = dualDataSource.getConnection();
conn1.createStatement().execute("CREATE DATABASE IF NOT EXISTS test");

Connection conn2 = dualDataSource.getConnection();
conn2.createStatement().execute("DROP TABLE IF EXISTS test.insert_test");
conn2.createStatement().execute("CREATE TABLE IF NOT EXISTS test.insert_test (i Int32, s String) ENGINE = TinyLog");
```
