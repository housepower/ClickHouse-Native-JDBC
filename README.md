ClickHouse Native JDBC
======================

[![Build Status](https://travis-ci.org/housepower/ClickHouse-Native-JDBC.svg?branch=master)](https://travis-ci.org/housepower/ClickHouse-Native-JDBC)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.housepower/clickhouse-native-jdbc-parent/badge.svg)](https://search.maven.org/search?q=clickhouse-native-jdbc)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/housepower/ClickHouse-Native-JDBC.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/housepower/ClickHouse-Native-JDBC/alerts/)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/housepower/ClickHouse-Native-JDBC.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/housepower/ClickHouse-Native-JDBC/context:java)
[![License](https://img.shields.io/github/license/housepower/ClickHouse-Native-JDBC)](https://github.com/housepower/ClickHouse-Native-JDBC/blob/master/LICENSE)

A Native JDBC library for accessing [ClickHouse](https://clickhouse.yandex/) in Java, also provide a library for 
integrating with [Apache Spark](https://github.com/apache/spark/).

Supported by [JetBrains Open Source License](https://www.jetbrains.com/?from=ClickHouse-Native-JDBC) 2020-2021. 

## JDBC Driver

### Requirements

- Java 8 or Java 11. 

**Notes:** We only do test with Java LTS versions.

### Differences from [yandex/clickhouse-jdbc](https://github.com/yandex/clickhouse-jdbc)

* Data is organized and compressed by columns.
* Implemented in the TCP Protocol, with higher performance than HTTP, here is the [benchmark report](Benchmark.md).

### Limitations

* Not support non-values format.
* Not support complex values expression, like `INSERT INTO test_table VALUES(toDate(123456))`.
* Not support more compression method, like `ZSTD`.

### Using

- Gradle
```groovy
// (recommended) shaded version, available since 2.3-stable
compile "com.github.housepower:clickhouse-native-jdbc-shaded:${clickhouse_native_jdbc_version}"

// normal version
compile "com.github.housepower:clickhouse-native-jdbc:${clickhouse_native_jdbc_version}"
```

- Maven

```xml
<!-- (recommended) shaded version, available since 2.3-stable -->
<dependency>
    <groupId>com.github.housepower</groupId>
    <artifactId>clickhouse-native-jdbc-shaded</artifactId>
    <version>${clickhouse-native-jdbc.version}</version>
</dependency>

<!-- normal version -->
<dependency>
    <groupId>com.github.housepower</groupId>
    <artifactId>clickhouse-native-jdbc</artifactId>
    <version>${clickhouse-native-jdbc.version}</version>
</dependency>
```

- Examples

Select query, see also [SimpleQuery](./examples/src/main/java/examples/SimpleQuery.java)

    Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000");
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT (number % 3 + 1) as n, sum(number) FROM numbers(10000000) GROUP BY n");
    while (rs.next()) {
        System.out.println(rs.getInt(1) + "\t" + rs.getLong(2));
    }
    // ... close resources

All DDL,DML queries, see also [ExecuteQuery](./examples/src/main/java/examples/ExecuteQuery.java)

    Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000");
    Statement stmt = connection.createStatement();
    stmt.executeQuery("drop table if exists test_jdbc_example");
    stmt.executeQuery("create table test_jdbc_example(day default toDate(toDateTime(timestamp)), timestamp UInt32, name String, impressions UInt32) Engine=MergeTree()");
    stmt.executeQuery("alter table test_jdbc_example add column costs Float32");
    stmt.executeQuery("drop table test_jdbc_example");
    // ... close resources

Batch insert query, see also [BatchQuery](./examples/src/main/java/examples/BatchQuery.java)

    Connection connection = DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000");
    PreparedStatement pstmt = connection.prepareStatement("INSERT INTO test_jdbc_example VALUES(?, ?, ?)");
    for (int i = 0; i < 200; i++) {
        pstmt.setDate(1, new Date(System.currentTimeMillis()));
        pstmt.setString(2, "Zhang San" + i);
        pstmt.setByte(3, (byte)i);
        pstmt.addBatch();
    }
    pstmt.executeBatch();
    stmt.executeQuery("drop table test_jdbc_example");
    // ... close resources

## Integration with Spark

### Requirements

- Java 8, Scala 2.11, Spark 2.4.x. 

**Notes:** Spark 2.3.x should also work fine. Actually we do test on both Java 8 and Java 11, 
but Spark official support on Java 11 since 3.0.0.

### Using

- Gradle
```groovy
// available since 2.4-stable
compile "com.github.housepower:clickhouse-integration-spark_2.11:${clickhouse_native_jdbc_version}"
```

- Maven

```xml
<!-- available since 2.4-stable -->
<dependency>
    <groupId>com.github.housepower</groupId>
    <artifactId>clickhouse-integration-spark_2.11</artifactId>
    <version>${clickhouse-native-jdbc.version}</version>
</dependency>
```

- Examples

Make sure register `ClickHouseDialects` before using it

    JdbcDialects.registerDialect(ClickHouseDialect)
    
Read from ClickHouse to DataFrame

    val df = spark.read
      .format("jdbc")
      .option("driver", "com.github.housepower.jdbc.ClickHouseDriver")
      .option("url", "jdbc:clickhouse://127.0.0.1:9000")
      .option("user", "default")
      .option("password", "")
      .option("dbtable", "db.test_source")
      .load

Write DataFrame to ClickHouse (support `truncate table`)

    df.write
      .format("jdbc")
      .mode("overwrite")
      .option("driver", "com.github.housepower.jdbc.ClickHouseDriver")
      .option("url", "jdbc:clickhouse://127.0.0.1:9000")
      .option("user", "default")
      .option("password", "")
      .option("dbtable", "db.test_target")
      .option("truncate", "true")
      .option("batchsize", 10000)
      .option("isolationLevel", "NONE")
      .save

See also [SparkOnClickHouseITest](clickhouse-integration/clickhouse-integration-spark/src/test/scala/com.github.housepower.jdbc.spark/SparkOnClickHouseITest.scala)

## Development

Get source code

    git clone https://github.com/housepower/ClickHouse-Native-JDBC.git
    
Compile and run Unit Tests

    mvn clean package
    
Run Integration Tests

    docker-compose up -d
    mvn clean verify -DskipTests
    
Run Benchmark

    docker-compose up -d
    mvn -Pbenchmark clean integration-test -DskipITs

Publish to Private Repository

    mvn clean deploy -Prelease -DskipTests -DskipITs \
        -Ddeploy.repo.snapshots.id={REPALCE_ME} \
        -Ddeploy.repo.snapshots.url={REPALCE_ME} \
        -Ddeploy.repo.release.id={REPALCE_ME} \
        -Ddeploy.repo.release.url={REPALCE_ME}

## License

This project is distributed under the terms of the Apache License (Version 2.0). See [LICENSE](LICENSE) for details.
