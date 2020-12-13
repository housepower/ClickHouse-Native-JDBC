## Datasource


### BalancedClickhouseDataSource

- We can put many connection configs to initial the `BalancedClickhouseDataSource`.
  And each time you call `getConnection`, it'll give back a random and health connection, this could be used for sharding data.

- Example codes:

```java
dataSource = new BalancedClickhouseDataSource("jdbc:clickhouse://127.0.0.1:9000");
doubleDataSource = new BalancedClickhouseDataSource("jdbc:clickhouse://127.0.0.1:9000,127.0.0.1:9000");

Connection connection = doubleDataSource.getConnection();
connection.createStatement().execute("CREATE DATABASE IF NOT EXISTS test");
connection = doubleDataSource.getConnection();
connection.createStatement().execute("DROP TABLE IF EXISTS test.insert_test");
connection.createStatement().execute(
        "CREATE TABLE IF NOT EXISTS test.insert_test (i Int32, s String) ENGINE = TinyLog");

```
