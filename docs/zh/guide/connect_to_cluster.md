## 连接到集群

### BalancedClickhouseDataSource

我们可以通过包含多个 clickhouse 实例地址的 JDBC URL 初始化 `BalancedClickhouseDataSource`，这样每次调用 `#getConnection` 时，
就可以获得的一个指向集群任一实例的健康连接。

目前, 我们只支持随机算法选择实例。

`BalancedClickhouseDataSource` 是线程安全的。

- 示例代码:

```java
DataSource singleDataSource = new BalancedClickhouseDataSource("jdbc:clickhouse://127.0.0.1:9000");

DataSource dualDataSource = new BalancedClickhouseDataSource("jdbc:clickhouse://127.0.0.1:9000,127.0.0.1:9000");

Connection conn1 = dualDataSource.getConnection();
conn1.createStatement().execute("CREATE DATABASE IF NOT EXISTS test");

Connection conn2 = dualDataSource.getConnection();
conn2.createStatement().execute("DROP TABLE IF EXISTS test.insert_test");
conn2.createStatement().execute("CREATE TABLE IF NOT EXISTS test.insert_test (i Int32, s String) ENGINE = TinyLog");
```
