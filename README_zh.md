ClickHouse Native JDBC
===

[![Build Status](https://github.com/housepower/ClickHouse-Native-JDBC/workflows/build/badge.svg?branch=master)](https://github.com/housepower/ClickHouse-Native-JDBC/actions?query=workflow%3Abuild+branch%3Amaster)
[![codecov.io](https://codecov.io/github/housepower/ClickHouse-Native-JDBC/coverage.svg?branch=master)](https://codecov.io/github/housepower/ClickHouse-Native-JDBC?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.housepower/clickhouse-native-jdbc-parent/badge.svg)](https://search.maven.org/search?q=com.github.housepower)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/housepower/ClickHouse-Native-JDBC.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/housepower/ClickHouse-Native-JDBC/alerts/)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/housepower/ClickHouse-Native-JDBC.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/housepower/ClickHouse-Native-JDBC/context:java)
[![License](https://img.shields.io/github/license/housepower/ClickHouse-Native-JDBC)](https://github.com/housepower/ClickHouse-Native-JDBC/blob/master/LICENSE)

[English](README.md) | 简体中文

## [项目主页](https://housepower.github.io/ClickHouse-Native-JDBC/zh/) | [GitHub](https://github.com/housepower/ClickHouse-Native-JDBC) | [码云](https://gitee.com/housepower/ClickHouse-Native-JDBC)

这是一个基于原生(TCP)协议实现的 JDBC 驱动，用于访问 [ClickHouse](https://clickhouse.yandex/) ，同时也支持与 [Apache Spark](https://github.com/apache/spark/) 的集成。

## 参与贡献

我们欢迎大家以各种形式参与项目贡献，可以是报告问题、完善文档、修复 bug、添加测试用例、实现新特性等。请参考 [贡献指南](CONTRIBUTE.md)。

本项目受 [JetBrains Open Source License](https://www.jetbrains.com/?from=ClickHouse-Native-JDBC) 2020-2021 赞助支持. 

## JDBC 驱动

### 使用要求

- Java 8/11. 

**注意:** 我们只基于 Java LTS 版本做测试。

### 与 [yandex/clickhouse-jdbc](https://github.com/yandex/clickhouse-jdbc) 驱动的不同点

* 写入时，数据按照列式格式组织并压缩
* 基于 TCP 协议实现，比 HTTP 协议更高效，参考 [性能测试报告](docs/dev/benchmark.md)。

### 限制

* 不支持复杂表达式语句的批量写入，如：`INSERT INTO test_table VALUES(toDate(123456))`，但查询不受影响。
* 不支持写入 non-values 格式，如 `TSV`。
* 不支持 `ZSTD` 压缩。

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

## 开源协议

Apache License (Version 2.0)。详情参考 [LICENSE](LICENSE).
