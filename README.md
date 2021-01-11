ClickHouse Native JDBC
===

[![Build Status](https://github.com/housepower/ClickHouse-Native-JDBC/workflows/build/badge.svg?branch=master)](https://github.com/housepower/ClickHouse-Native-JDBC/actions?query=workflow%3Abuild+branch%3Amaster)
[![codecov.io](https://codecov.io/github/housepower/ClickHouse-Native-JDBC/coverage.svg?branch=master)](https://codecov.io/github/housepower/ClickHouse-Native-JDBC?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.housepower/clickhouse-native-jdbc-parent/badge.svg)](https://search.maven.org/search?q=com.github.housepower)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/housepower/ClickHouse-Native-JDBC.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/housepower/ClickHouse-Native-JDBC/alerts/)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/housepower/ClickHouse-Native-JDBC.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/housepower/ClickHouse-Native-JDBC/context:java)
[![License](https://img.shields.io/github/license/housepower/ClickHouse-Native-JDBC)](https://github.com/housepower/ClickHouse-Native-JDBC/blob/master/LICENSE)

English | [简体中文](README_zh.md)

## [Home Page](https://housepower.github.io/ClickHouse-Native-JDBC/) | [GitHub](https://github.com/housepower/ClickHouse-Native-JDBC) | [Gitee](https://gitee.com/housepower/ClickHouse-Native-JDBC)

A Native JDBC library for accessing [ClickHouse](https://clickhouse.yandex/) in Java, also provide a library for 
integrating with [Apache Spark](https://github.com/apache/spark/).

## CONTRIBUTE

We welcome anyone that wants to help out in any way, whether that includes reporting problems, helping with documentations, or contributing code changes to fix bugs, add tests, or implement new features. Please follow [Contributing Guide](CONTRIBUTE.md).

Supported by [JetBrains Open Source License](https://www.jetbrains.com/?from=ClickHouse-Native-JDBC) 2020-2021. 

## JDBC Driver

### Requirements

- Java 8/11. 

**Notes:** We only do test with Java LTS versions.

### Differences from [yandex/clickhouse-jdbc](https://github.com/yandex/clickhouse-jdbc)

* Data is organized and compressed by columns.
* Implemented in the TCP Protocol, with higher performance than HTTP, here is the [benchmark report](docs/dev/benchmark.md).

### Limitations

* Not support insert complex values expression, like `INSERT INTO test_table VALUES(toDate(123456))`, but query is ok.
* Not support insert non-values format, like `TSV`.
* Not support more compression method, like `ZSTD`.

### Import

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

## License

This project is distributed under the terms of the Apache License (Version 2.0). See [LICENSE](LICENSE) for details.
