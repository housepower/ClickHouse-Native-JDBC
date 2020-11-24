## Connection Pool

This driver should work properly with most connection pool, we do test with the most popular 3 pools:

### HikariCP

Add dependency in Maven `pom.xml`.

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>${hikari-cp.version}</version>
</dependency>
```

Use simple `HikariDataSource`.

```java
HikariConfig conf = new HikariConfig();
conf.setJdbcUrl(getJdbcUrl());
conf.setDriverClassName(DRIVER_CLASS_NAME);
try (HikariDataSource ds = new HikariDataSource(conf)) {
    runSql(ds);
}
```

Use `HikariDataSource` wrap `BalancedClickhouseDataSource` to connect clickhouse cluster.

```java
DataSource balancedCkDs = new BalancedClickhouseDataSource(getJdbcUrl());
HikariConfig conf = new HikariConfig();
conf.setDataSource(balancedCkDs);
try (HikariDataSource ds = new HikariDataSource(conf)) {
    runSql(ds);
}
```


### Alibaba Druid

Add dependency in Maven `pom.xml`

```xml  
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>${alibaba-druid.version}</version>
</dependency>
```

Use `DruidDataSource`.

```java
Properties prop = new Properties();
prop.put("url", getJdbcUrl());
prop.put("driverClassName", DRIVER_CLASS_NAME);
try (DruidDataSource ds = (DruidDataSource) DruidDataSourceFactory.createDataSource(prop)) {
    runSql(ds);
}
```

### Apache DBCP2

Add dependency in Maven `pom.xml`

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-dbcp2</artifactId>
    <version>${apache-dbcp2.version}</version>
</dependency>
```

Use `BasicDataSource`.

```java
Properties prop = new Properties();
prop.put("url", getJdbcUrl());
prop.put("driverClassName", DRIVER_CLASS_NAME);
try (BasicDataSource ds = BasicDataSourceFactory.createDataSource(prop)) {
    runSql(ds);
}
```