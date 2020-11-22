Release Notes
===

v2.4.2 (Nov 20, 2020)
---
### Changelog
- Trim space before parsing SQL
- Fix DateTime64 nanos and scale (#217)
- Reset connection STATE when recreate connection (#221)
- Skip remaining responses when consume PingResponse (#224)
- Minor tunes


v2.4.1 (Nov 15, 2020)
---
### Changelog
- Fix invalid package clickhouse-integration-spark_2.12 (#206)


v2.4.0 (Nov 9, 2020)
---
### Highlight
- Since this release v2.4.0, we switch to [semantic versioning](https://semver.org/), and the bug fix version would be available quickly.
- We introduce a new module `clickhouse-integration-spark` base on Spark Jdbc DataSource API for integrating with Spark, check detail at [README](https://github.com/housepower/ClickHouse-Native-JDBC#integration-with-spark)
- In the past few weeks, we were more focus on code quality, fixed all [LGTM](https://lgtm.com/projects/g/housepower/ClickHouse-Native-JDBC/alerts/) alerts and got A+ score.

### Changelog
- Implement `clickhouse-integration-spark` base on Spark Jdbc DataSource API (#170 #184)
- Batch insert support nullable types in arrays and nested types (#144 #194)
- Support Boolean (#196)
- Fix timezone issue (#195)
- Fix potential concurrence issue (#191)
- Code refactor, fixed all [LGTM](https://lgtm.com/projects/g/housepower/ClickHouse-Native-JDBC/alerts/) alerts and got A+ score.
- Refactor `ClickHouseStatement` inheritance tree (#201)
- Migrate to mockito3 (#175)
- Migrate to org.lz4:lz4-java (#174)
- Migrate to Junit5 (#171)
- Bump yandex/clickhouse-jdbc 0.2.4
- Bump jmh 1.26
- Enable checkstyle (#172)
- Enable code coverage (#190)
- Improve Docs and Readme
- Add missing license headers


v2.3 (Oct 25, 2020)
---
### Highlight
- Provide shaded version `clickhouse-native-jdbc-shaded` since this release, see detail at README.md

### Changelog
- Fixed `SQLFeatureNotSupportedException` in several scenarios (#142 thanks @dcastanier) (thanks @sundy-li)
- Fixed return value for executeBatch in PreparedInsertStatement (#145 thanks @tauitdnmd)
- Fixed jdbc url parse (#148 thanks @tauitdnmd)
- Support zoned datetime DataType, i.e. `DateTime(Asia/Shanghai)` (#158 thanks @sundy-li)
- Refactor project to multi modules (#156 thanks @pan3793)
- Remove `joda-time`, migrate `joda-time` and legacy `java.util.Date` to `java.time` (#164 thanks @pan3793)
- Integration Test against Java 8, Java 11 (thanks @sundy-li)
- Integration Test with Spark 2.4.7&Scala 2.11 (thanks @sundy-li)
- Bump junit from 4.11 to 4.13.1


::: tip
You can find early releases at [GitHub Release Page](https://github.com/housepower/ClickHouse-Native-JDBC/releases)
:::
