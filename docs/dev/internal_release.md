Internal Release Manuel
=======================

::: tip
Internal Release means deploy to private Nexus Repository. Please make sure you are granted to access 
your company private Nexus Repository.
:::

## Setup Nexus Authentication

Edit and put below configurations into `~/.m2/settings.xml`

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

## Package and Deploy to Nexus

```shell script
mvn clean deploy -Prelease -DskipTests -DskipITs \
    -Ddeploy.repo.snapshots.id={REPALCE_ME} \
    -Ddeploy.repo.snapshots.url={REPALCE_ME} \
    -Ddeploy.repo.release.id={REPALCE_ME} \
    -Ddeploy.repo.release.url={REPALCE_ME}
```

::: tip
To force refresh snapshot dependencies:
- Maven: `mvn clean compile --update-snapshots`
- Gradle: `gradlew clean build --refresh-dependencies`
:::