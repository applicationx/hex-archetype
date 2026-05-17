# Archetype Install And Upgrade

This guide explains how to install, publish, upgrade, and validate the `com.appx:hexagonal-spring-boot-archetype` archetype.

Use this when making the archetype available for local development, CI, or other machines through AppX Nexus.

## Requirements

- JDK `25`
- Maven `3.9.15+`
- For Nexus publishing: `NEXUS_APPX_PASSWORD` exported in the shell
- Maven settings with these server ids:
  - `nexus-public`
  - `nexus-snapshots`
  - `nexus-releases`

## Local Install

Use local install when you are working on this repository and want to generate projects on the same machine.

```bash
cd /home/appx/github/hex-archetype
mvn -B -ntp clean install
```

This installs the archetype into the current user's local Maven repository and updates the local archetype catalog.

Generate a project from the local install:

```bash
mkdir -p /home/appx/github/test-artifact-repo
cd /home/appx/github/test-artifact-repo

mvn -B -ntp archetype:generate \
  -DarchetypeGroupId=com.appx \
  -DarchetypeArtifactId=hexagonal-spring-boot-archetype \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=com.appx \
  -DartifactId=sample-customer-service \
  -Dversion=0.1.0-SNAPSHOT \
  -Dpackage=com.appx.sample \
  -DjavaVersion=25 \
  -DspringBootVersion=4.0.6 \
  -DspringCloudVersion=2025.1.1 \
  -DinteractiveMode=false
```

Validate the generated project:

```bash
cd /home/appx/github/test-artifact-repo/sample-customer-service
mvn -B -ntp verify
```

## Publish To AppX Nexus

Publish to Nexus when other machines, CI jobs, or teammates should use the archetype without cloning this repository first.

The root `pom.xml` publishes:

- Snapshots to `https://nexus.appx-labs.com/repository/maven-snapshots/`
- Releases to `https://nexus.appx-labs.com/repository/maven-releases/`

Publish the current snapshot:

```bash
cd /home/appx/github/hex-archetype
mvn -B -ntp clean deploy
```

If normal `deploy` does not show upload lines for the `maven-archetype` artifact, publish explicitly:

```bash
mvn -B -ntp org.apache.maven.plugins:maven-deploy-plugin:3.1.4:deploy-file \
  -Dfile=target/hexagonal-spring-boot-archetype-1.0.0-SNAPSHOT.jar \
  -DpomFile=pom.xml \
  -DrepositoryId=nexus-snapshots \
  -Durl=https://nexus.appx-labs.com/repository/maven-snapshots/
```

## Nexus Resolution Setup

If `~/.m2/settings.xml` mirrors all repositories through `maven-public`, snapshot archetype resolution can fail if `maven-public` does not expose the snapshot. Configure the mirror to exclude `nexus-snapshots` and activate the snapshot repository directly.

Expected mirror shape:

```xml
<mirror>
  <id>nexus-public</id>
  <name>appx Nexus Maven Public</name>
  <url>https://maven.appx-labs.com/</url>
  <mirrorOf>external:*,!nexus-snapshots</mirrorOf>
</mirror>
```

Expected active snapshot repository:

```xml
<profile>
  <id>appx-nexus-snapshots</id>
  <repositories>
    <repository>
      <id>nexus-snapshots</id>
      <url>https://nexus.appx-labs.com/repository/maven-snapshots/</url>
      <releases>
        <enabled>false</enabled>
      </releases>
      <snapshots>
        <enabled>true</enabled>
      </snapshots>
    </repository>
  </repositories>
</profile>
```

## Validate Nexus Publication

Use an empty temporary Maven repository so the check cannot succeed from the normal local cache.

```bash
rm -rf /tmp/hex-archetype-nexus-check

mvn -B -ntp dependency:get \
  -Dmaven.repo.local=/tmp/hex-archetype-nexus-check \
  -Dartifact=com.appx:hexagonal-spring-boot-archetype:1.0.0-SNAPSHOT
```

Generate from Nexus using a temporary Maven repository:

```bash
rm -rf /tmp/hex-archetype-nexus-generate
mkdir -p /home/appx/github/test-artifact-repo
cd /home/appx/github/test-artifact-repo

mvn -B -ntp archetype:generate \
  -Dmaven.repo.local=/tmp/hex-archetype-nexus-generate \
  -DarchetypeCatalog=remote \
  -DarchetypeGroupId=com.appx \
  -DarchetypeArtifactId=hexagonal-spring-boot-archetype \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=com.appx \
  -DartifactId=sample-from-nexus-service \
  -Dversion=0.1.0-SNAPSHOT \
  -Dpackage=com.appx.nexussample \
  -DjavaVersion=25 \
  -DspringBootVersion=4.0.6 \
  -DspringCloudVersion=2025.1.1 \
  -DinteractiveMode=false
```

Then build the generated project:

```bash
cd /home/appx/github/test-artifact-repo/sample-from-nexus-service
mvn -B -ntp verify
```

## Upgrade Workflow

Use this workflow when changing the archetype template.

1. Update files under `src/main/resources/archetype-resources/`.
2. Update `src/main/resources/META-INF/maven/archetype-metadata.xml` for every added or moved generated file.
3. Update root `README.md` and generated module `README.md` files when behavior or module responsibilities change.
4. For deployment template changes, compare against `/home/appx/github/spring-gateway-base/.github/workflows/deploy.yml`, `/home/appx/github/appx-web/.github/workflows/deploy.yml`, and the matching `k3s-dev/manifests/argocd/*-application.yaml` files before publishing.
5. Run local install:

```bash
mvn -B -ntp clean install
```

6. Generate a disposable project outside this repository.
7. Run `mvn -B -ntp verify` in the generated project.
8. Publish to Nexus with `mvn -B -ntp clean deploy`, or explicit `deploy-file` if needed.
9. Validate Nexus resolution with an empty `-Dmaven.repo.local` path.

## Versioning Guidance

- Keep active development on `1.0.0-SNAPSHOT` until the archetype is stable.
- Use a release version such as `1.0.0` only when generated projects are expected to be reproducible from a fixed archetype.
- After releasing `1.0.0`, move the repository to the next snapshot, for example `1.0.1-SNAPSHOT`.

## Cleanup

The validation repositories under `/home/appx/github/test-artifact-repo` are disposable.

```bash
rm -rf /home/appx/github/test-artifact-repo
```
