内部发布手册
==========

::: tip
内部发布是指部署到私服 Nexus 仓库. 请确保你有公司私服的访问权限.
:::

## 设置私服 Nexus 认证

编辑并将下面的配置放入 `~/.m2/settings.xml`

```xml
<servers>
  <server>
    <id>{repo-snapshots-id}</id>
    <username>{username}</username>
    <password>{password}</password>
  </server>
  <server>
    <id>{repo-release-id}</id>
    <username>{username}</username>
    <password>{password}</password>
  </server>
</servers>
```

## 打包并部署到私服 Nexus

```shell script
mvn clean deploy -Prelease -DskipTests -DskipITs \
    -Ddeploy.repo.snapshots.id={REPALCE_ME} \
    -Ddeploy.repo.snapshots.url={REPALCE_ME} \
    -Ddeploy.repo.release.id={REPALCE_ME} \
    -Ddeploy.repo.release.url={REPALCE_ME}
```

::: tip
用以下命令强制刷新 SNAPSHOT 依赖:
- Maven: `mvn clean compile –update-snapshots`
- Gradle: `gradlew clean build --refresh-dependencies`
:::