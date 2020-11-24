ClickHouse Native JDBC
======================
A Native JDBC library for accessing [ClickHouse](https://clickhouse.yandex/) in Java, also provide a library for 
integrating with [Apache Spark](https://github.com/apache/spark/).

Supported by [JetBrains Open Source License](https://www.jetbrains.com/?from=ClickHouse-Native-JDBC) 2020-2021. 

## JDBC Driver

### Differences from [yandex/clickhouse-jdbc](https://github.com/yandex/clickhouse-jdbc)

* Data is organized and compressed by columns.
* Implemented in the TCP Protocol, with higher performance than HTTP, here is the [benchmark report](../dev/benchmark.md).

### Limitations

* Not support non-values format.
* Not support complex values expression, like `INSERT INTO test_table VALUES(toDate(123456))`.
* Not support more compression method, like `ZSTD`.

## Spark Integration

Currently，the implementation based on Spark JDBC API, support data type mapping, auto create table, truncate table, write, read, etc.

## License

This project is distributed under the terms of the Apache License (Version 2.0). See [LICENSE](https://github.com/housepower/ClickHouse-Native-JDBC/LICENSE) for details.
