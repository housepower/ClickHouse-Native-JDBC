## JDBC 驱动

### 使用要求

- Java 8/11. 

**注意:** 我们只基于 Java LTS 版本做测试。

### 导入包

- Gradle
```groovy
// (推荐) shaded 版本，自 2.3-stable 起可用
compile "com.github.housepower:clickhouse-native-jdbc-shaded:${clickhouse_native_jdbc_version}"

// 常规版本
compile "com.github.housepower:clickhouse-native-jdbc:${clickhouse_native_jdbc_version}"
```

- Maven

```xml
<!-- (推荐) shaded 版本，自 2.3-stable 起可用 -->
<dependency>
    <groupId>com.github.housepower</groupId>
    <artifactId>clickhouse-native-jdbc-shaded</artifactId>
    <version>${clickhouse-native-jdbc.version}</version>
</dependency>

<!-- 常规版本 -->
<dependency>
    <groupId>com.github.housepower</groupId>
    <artifactId>clickhouse-native-jdbc</artifactId>
    <version>${clickhouse-native-jdbc.version}</version>
</dependency>
```


### 示例

查询示例，更多参考 [SimpleQuery](https://github.com/housepower/ClickHouse-Native-JDBC/tree/master/examples/src/main/java/examples/SimpleQuery.java)

```java
try (Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000")) {
    try (Statement stmt = connection.createStatement()) {
        try (ResultSet rs = stmt.executeQuery(
                "SELECT (number % 3 + 1) as n, sum(number) FROM numbers(10000000) GROUP BY n")) {
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "\t" + rs.getLong(2));
            }
        }
    }
}
```

DDL、DML 示例，更多参考 [ExecuteQuery](https://github.com/housepower/ClickHouse-Native-JDBC/tree/master/examples/src/main/java/examples/ExecuteQuery.java)

```java
try (Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000")) {
    try (Statement stmt = connection.createStatement()) {
        stmt.executeQuery("drop table if exists test_jdbc_example");
        stmt.executeQuery("create table test_jdbc_example(" +
                "day default toDate( toDateTime(timestamp) ), " +
                "timestamp UInt32, " +
                "name String, " +
                "impressions UInt32" +
                ") Engine=MergeTree(day, (timestamp, name), 8192)");
        stmt.executeQuery("alter table test_jdbc_example add column costs Float32");
        stmt.executeQuery("drop table test_jdbc_example");
    }
}
```

批量插入示例，更多参考 [BatchQuery](https://github.com/housepower/ClickHouse-Native-JDBC/tree/master/examples/src/main/java/examples/BatchQuery.java)

```java
try (Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000")) {
    try (Statement stmt = connection.createStatement()) {
        try (ResultSet rs = stmt.executeQuery("drop table if exists test_jdbc_example")) {
            System.out.println(rs.next());
        }
        try (ResultSet rs = stmt.executeQuery("create table test_jdbc_example(day Date, name String, age UInt8) Engine=Log")) {
            System.out.println(rs.next());
        }
        try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO test_jdbc_example VALUES(?, ?, ?)")) {
            for (int i = 1; i <= 200; i++) {
                pstmt.setDate(1, new Date(System.currentTimeMillis()));
                if (i % 2 == 0)
                    pstmt.setString(2, "Zhang San" + i);
                else
                    pstmt.setString(2, "Zhang San");
                pstmt.setByte(3, (byte) ((i % 4) * 15));
                System.out.println(pstmt);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }

        try (PreparedStatement pstmt = connection.prepareStatement("select count(*) from test_jdbc_example where age>? and age<=?")) {
            pstmt.setByte(1, (byte) 10);
            pstmt.setByte(2, (byte) 30);
            printCount(pstmt);
        }

        try (PreparedStatement pstmt = connection.prepareStatement("select count(*) from test_jdbc_example where name=?")) {
            pstmt.setString(1, "Zhang San");
            printCount(pstmt);
        }
        try (ResultSet rs = stmt.executeQuery("drop table test_jdbc_example")) {
            System.out.println(rs.next());
        }
    }
}
```
