## Spark 集成

### 使用要求

- Java 8, Scala 2.11/2.12, Spark 2.4.x
- 或者 Java 8/11, Scala 2.12, Spark 3.0.x

**注意:** Spark 2.3.x(EOL) 理论上也支持。但我们只对 Java 8 和 Java 11 做测试，Spark 自 3.0.0 起官方支持 Java 11。

### 导入包

- Gradle

```groovy
// 自 2.4.0 起可用
compile "com.github.housepower:clickhouse-integration-spark_2.11:${clickhouse_native_jdbc_version}"
```

- Maven

```xml
<!-- 自 2.4.0 起可用 -->
<dependency>
    <groupId>com.github.housepower</groupId>
    <artifactId>clickhouse-integration-spark_2.11</artifactId>
    <version>${clickhouse-native-jdbc.version}</version>
</dependency>
```

### 示例

请确保在使用前注册 `ClickHouseDialects` 

```scala
    JdbcDialects.registerDialect(ClickHouseDialect)
```

读取 ClickHouse 表数据到 DataFrame

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

将 DataFrame 写入 ClickHouse 表 (支持 `truncate table`)

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

更多参考 [SparkOnClickHouseITest](https://github.com/housepower/ClickHouse-Native-JDBC/clickhouse-integration/clickhouse-integration-spark/src/test/scala/com.github.housepower.jdbc.spark/SparkOnClickHouseITest.scala)
