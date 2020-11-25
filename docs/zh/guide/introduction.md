ClickHouse Native JDBC
======================
这是一个用原生TCP协议实现的JDBC驱动，主要用来访问 [ClickHouse](https://clickhouse.yandex/)，同时提供了和 [Apache Spark](https://github.com/apache/spark/) 的集成。

Supported by [JetBrains Open Source License](https://www.jetbrains.com/?from=ClickHouse-Native-JDBC) 2020-2021. 

## JDBC 驱动

### 和 [yandex/clickhouse-jdbc](https://github.com/yandex/clickhouse-jdbc) 的不同点

* 数据按照列式格式写入
* 用TCP实现，比HTTP协议更高效，参考 [benchmark report](docs/dev/benchmark.md)。

### 限制

* 不支持 non-values 格式。
* 不支持复杂表达式语句的批量写入，如： `INSERT INTO test_table VALUES(toDate(123456))`。
* 不支持 `ZSTD` 压缩。

## Spark 集成

目前实现了和 Spark JDBC API 的打通，如数据类型映射，自动建表，自动情况表，读写等。

## 证书

Apache License (Version 2.0). 更多参考 [LICENSE](https://github.com/housepower/ClickHouse-Native-JDBC/LICENSE) for details.
