[![Build Status](https://travis-ci.org/housepower/ClickHouse-Native-JDBC.svg?branch=master)](https://travis-ci.org/housepower/ClickHouse-Native-JDBC)

# ClickHouse-Native-JDBC

This is a native JDBC library for accessing [ClickHouse](https://clickhouse.yandex/) in Java.


## Use the latest code

```
    git clone git@github.com:housepower/ClickHouse-Native-JDBC.git
    cd ClickHouse-Native-JDBC
    mvn clean package
    #build single jar with dependencies
    mvn clean package assembly:single -Dmaven.skip.assembly=false
```

## Maven central

```java
<dependency>
    <groupId>com.github.housepower</groupId>
    <artifactId>clickhouse-native-jdbc</artifactId>
    <version>2.2-stable</version>
</dependency>
```

## Support Java8 or above

## Differences from [Yandex/Clickhouse-JDBC](https://github.com/yandex/clickhouse-jdbc)
* Data is organized and compressed by columns
* We implemented it using the TCP Protocol, with higher performance than HTTP, here is the [benchmark](./Benchmark.md).

## Not Supported
* Non-values format
* Complex values expression, Like `INSERT INTO test_table VALUES(toDate(123456))`
* More compression method, like `ZSTD`

## Example

Select query, see also [SimpleQuery.java](./src/main/java/examples/SimpleQuery.java)
```java
    Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000");

    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT (number % 3 + 1) as n, sum(number) FROM numbers(10000000) GROUP BY n");

    while (rs.next()) {
      System.out.println(rs.getInt(1) + "\t" + rs.getLong(2));
    }
```

All DDL,DML queries, see also [ExecuteQuery.java](./src/main/java/examples/ExecuteQuery.java)

```java
    Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000");

    Statement stmt = connection.createStatement();
    stmt.executeQuery("drop table if exists test_jdbc_example");
    stmt.executeQuery("create table test_jdbc_example(day default toDate( toDateTime(timestamp) ), timestamp UInt32, name String, impressions UInt32) Engine=MergeTree(day, (timestamp, name), 8192)");
    stmt.executeQuery("alter table test_jdbc_example add column costs Float32");
    stmt.executeQuery("drop table test_jdbc_example");
```

Batch insert query, see also [BatchQuery.java](./src/main/java/examples/BatchQuery.java)

``` java
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
```
