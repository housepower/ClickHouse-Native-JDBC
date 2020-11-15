## Setup GPG

https://imtianx.cn/2019/05/29/gpg_an_zhuang_yu_shi_yong/

## Setup Sonatype Authentication

Edit and put below configurations into `~/.m2/settings.xml`

```xml
<servers>
  <server>
    <id>sonatype-nexus-snapshots</id>
    <username>xxx</username>
    <password>xxx</password>
  </server>
  <server>
    <id>sonatype-nexus-staging</id>
    <username>xxx</username>
    <password>xxx</password>
  </server>
</servers>
```

## Cut Branch

It's only need for **feature release**, you should cut a branch from master.

```shell script
git checkout -b 2.5
```

## Bump Version

prepare release

```shell script
mvn versions:set -DnewVersion=2.5.0
mvn versions:commit
git commit -am '(release) prepare release v2.5.0-rc0'
```

prepare for next development iteration

```shell script
mvn versions:set -DnewVersion=2.6.0-SNAPSHOT
mvn versions:commit
git commit -am '(release) prepare for next development iteration'
```

## Package and Deploy to Sonatype

```shell script
mvn clean deploy -DskipTests -Prelease
mvn clean deploy -DskipTests -Prelease -Pscala-2.12
```

## Publish Release

Verify, Close and Release to Maven Central Repository at https://oss.sonatype.org/#stagingRepositories

## Announce on GitHub

https://github.com/housepower/ClickHouse-Native-JDBC/releases