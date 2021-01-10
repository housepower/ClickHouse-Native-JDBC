Release Notes
===

v2.5.2 (Jan 11, 2021)
---
### Changelog
- (bugfix) Fix ResultSetMetaData.isSigned (#281 #283)
- (bugfix) Fix LoggerFactory#getLogger (#287)
- (enhance) Set ClickHouseSQLException errorCode correctly (#290) Thanks @baibaichen
- (feature) Rework stuffs related to time api (#288) Thanks @sbouchex

v2.5.1 (Jan 4, 2021)
---
### Changelog
- (bugfix) Fix Negative Decimal serde (#279)

v2.5.0 (Dec 27, 2020)
---
### Highlight
- The project static website is available at [Home Page](https://housepower.github.io/ClickHouse-Native-JDBC)
- The China mirror repo is available at [Gitee](https://gitee.com/housepower/ClickHouse-Native-JDBC)
- Implemented `BalancedClickhouseDataSource` to support connect to clickhouse cluster
- Now the JDBC driver works with DataGrip and DBeaver
- Introduced a log facade which prefer to bind `slf4j` if it exists in classpath, otherwise fallback to JDK `Logger`, and added logs for tracing, debugging and warning

This release contains lots of internal refactors, and currently, this driver guarantee none stable API except JDBC API itself, please be careful if you use the internal API which not belongs to JDBC standard interfaces.

### Changelog
- (feature) Implement `BalancedClickhouseDataSource` to support multi instances (#205)
- (bugfix) Fix invalid package clickhouse-integration-spark_2.12 (#206)
- (feature) Implement `ClickHouseConnection` catalog and schema method (#208)
- (enhance) Enhance `Number` process
- (build) Disable deploy `examples` module
- (test) Integration tests with connection pool (#212)
- (feature) Support `charset` parameter (#213)
- (feature) Implement `ClickHousePreparedStatment` `#setBytes` and `#getBytes` (#213)
- (enhance) Trim space before parsing SQL
- (feature) Support `tcp_keep_alive` parameter
- (bugfix) Fix DateTime64 nanos and scale (#217)
- (bugfix) Reset connection `STATE` when recreate connection (#221)
- (bugfix) Skip remaining response when ping (#224)
- (refactor) Decouple `RequestOrResponse` and rename `PhysicalConnection` to `NativeClient` (#225)
- (feature) Introduce log classes to avoid hard dependence on `slf4j` (#231)
- (build) Add `debug` Maven profile (#235)
- (enhance) Add LRU cache for lexer `IDataType` (#236)
- (feature) Refactor and implement ClickHouseDatabaseMetadata (#233)
- (feature) Support type alias (#240)
- (bugfix) Explicit upcast ByteBuffer to Buffer (#243)
- (deps) Bump ini from 1.3.5 to 1.3.8 (#250)
- (feature) Support Decimal256 && Decimal128 (#239)
- (refactor) Refactor and fixup (#246)
- (deps) bump alibaba druid 1.2.4
- (deps) bump slf4j 1.7.30
- (deps) bump mockito 3.6.28
- (deps) bump jmh 1.27
- (bugfix) Cast correctly numbers to get a boolean (#256)
- (bugfix) Added check against null data on `ClickHouseResultSet#getXXX` (#258) (#259)
- (feature) Handle Nothing datatype (#261)
- (bugfix) `ClickHouseConnection#connect` should return null for unacceptable url (#267)
- (enhance) `BalancedClickhouseDataSource` support optional database url
- (test) Added user & password for ClickHouse unit tests (#264)
- (feature) Support arbitrary settings (#268)
- (refactor) Rewrite `StringView` implements `CharSequence` (#270)
- (workflow) Auto report benchmark report.

v2.4.4 (Dec 27, 2020)
---
### Changelog
- (backport) cast correctly numbers to get a boolean (#256)
- (backport) enhance null check on ClickHouseResultSet#getXXX (#259)

v2.4.3 (Dec 15, 2020)
---
### Changelog
- (backport) valid connection by ping instead of SELECT 1
- (backport) explicit upcast ByteBuffer to Buffer (#243)
- (backport) fix Connection#getCatalog (#249)
- (backport) fix ClickHouseStatement#getUpdateCount
- (backport) fix ClickHouseStatement#setMaxRows
- (backport) ClickHouseResultSet#getString support all types
- (backport) implement ClickHouseResultSet#getBoolean
- (backport) implement ClickHouseResultSet#isBeforeFirst, #isAfterLast, #isFirst etc.
- ClickHouseConnection#getMetaData return null instead of throw exception


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
