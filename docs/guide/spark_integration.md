## Integration with Spark

### Requirements

- Java 8, Scala 2.11/2.12, Spark 2.4.x
- Or Java 8/11, Scala 2.12, Spark 3.0.x

**Notes:** Spark 2.3.x(EOL) should also work fine. Actually we do test on both Java 8 and Java 11, 
but Spark official support on Java 11 since 3.0.0.

### Import

- Gradle

```groovy
// available since 2.4.0
compile "com.github.housepower:clickhouse-integration-spark_2.11:${clickhouse_native_jdbc_version}"
```

- Maven

```xml
<!-- available since 2.4.0 -->
<dependency>
    <groupId>com.github.housepower</groupId>
    <artifactId>clickhouse-integration-spark_2.11</artifactId>
    <version>${clickhouse-native-jdbc.version}</version>
</dependency>
```

### Examples

Make sure register `ClickHouseDialects` before using it

```scala
    JdbcDialects.registerDialect(ClickHouseDialect)
```

Read from ClickHouse to DataFrame

```scala
val df = spark.read
    .format("jdbc")
    .option("driver", "com.github.housepower.jdbc.ClickHouseDriver")
    .option("url", "jdbc:clickhouse://127.0.0.1:9000")
    .option("user", "default")
    .option("password", "")
    .option("dbtable", "db.test_source")
    .load
```

Write DataFrame to ClickHouse (support `truncate table`)

```scala
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
```

See also [SparkOnClickHouseITest](https://github.com/housepower/ClickHouse-Native-JDBC/clickhouse-integration/clickhouse-integration-spark/src/test/scala/com.github.housepower.jdbc.spark/SparkOnClickHouseITest.scala)
