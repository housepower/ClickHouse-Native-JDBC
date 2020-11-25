## 连接池

这个驱动兼容大多数连接池，我们就最常用的三种做了测试：

### HikariCP

在 Maven `pom.xml` 添加依赖.

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>${hikari-cp.version}</version>
</dependency>
```

使用普通的 `HikariDataSource`.

```java
HikariConfig conf = new HikariConfig();
conf.setJdbcUrl(getJdbcUrl());
conf.setDriverClassName(DRIVER_CLASS_NAME);
try (HikariDataSource ds = new HikariDataSource(conf)) {
    runSql(ds);
}
```

使用 `HikariDataSource` 包装 `BalancedClickhouseDataSource` 来连接 ClickHouse 集群.

```java
DataSource balancedCkDs = new BalancedClickhouseDataSource(getJdbcUrl());
HikariConfig conf = new HikariConfig();
conf.setDataSource(balancedCkDs);
try (HikariDataSource ds = new HikariDataSource(conf)) {
    runSql(ds);
}
```


### Alibaba Druid

在 Maven `pom.xml` 添加依赖.

```xml  
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>${alibaba-druid.version}</version>
</dependency>
```

使用 `DruidDataSource`.

```java
Properties prop = new Properties();
prop.put("url", getJdbcUrl());
prop.put("driverClassName", DRIVER_CLASS_NAME);
try (DruidDataSource ds = (DruidDataSource) DruidDataSourceFactory.createDataSource(prop)) {
    runSql(ds);
}
```

### Apache DBCP2

在 Maven `pom.xml` 添加依赖.

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-dbcp2</artifactId>
    <version>${apache-dbcp2.version}</version>
</dependency>
```

使用 `BasicDataSource`.

```java
Properties prop = new Properties();
prop.put("url", getJdbcUrl());
prop.put("driverClassName", DRIVER_CLASS_NAME);
try (BasicDataSource ds = BasicDataSourceFactory.createDataSource(prop)) {
    runSql(ds);
}
```
