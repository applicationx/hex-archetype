# Hexagonal Spring Boot Maven Archetype

This repository provides a Maven archetype that generates a multi-module hexagonal architecture project for Spring Boot.

Default generated stack:
- Spring Boot: `4.0.6`
- Java: `25`
- Spring Cloud: `2025.1.1`
- springdoc-openapi: `3.0.3`
- Maven: `3.9.15+`
- Maven POM model: `4.0.0`
- Modules: `domain`, `application`, `adapters/inbound-rest`, `adapters/inbound-kafka`, `adapters/outbound-jpa`, `webapp` (Spring Boot + React/Vite frontend), `client`
- Kubernetes config: Spring Cloud Config Client in `webapp`, with AppX Config Server defaults and shared-chart values example.

## Prerequisites

- Maven `3.9.15` or newer installed
- JDK `25` installed

## Install Archetype Locally

From the archetype repository root:

```bash
mvn -B -ntp clean install
```

This installs the archetype into your local Maven repository.

## Generate a Project from the Archetype

```bash
mvn -B -ntp archetype:generate \
  -DarchetypeGroupId=com.appx \
  -DarchetypeArtifactId=hexagonal-spring-boot-archetype \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=com.appx \
  -DartifactId=customer-service \
  -Dversion=0.1.0-SNAPSHOT \
  -Dpackage=com.appx.customers \
  -DjavaVersion=25 \
  -DspringBootVersion=4.0.6 \
  -DspringCloudVersion=2025.1.1 \
  -DinteractiveMode=false
```

## Build Generated Project

```bash
cd customer-service
mvn -B -ntp verify
```

## Publish Archetype To AppX Nexus

`~/.m2/settings.xml` must define `nexus-snapshots` and `nexus-releases` credentials. This machine expects the password through `NEXUS_APPX_PASSWORD`.

See [docs/ARCHETYPE_INSTALL_UPGRADE.md](docs/ARCHETYPE_INSTALL_UPGRADE.md) for the full install, publish, upgrade, and Nexus validation workflow.

```bash
mvn -B -ntp deploy
```

Snapshot versions such as `1.0.0-SNAPSHOT` deploy to `https://nexus.appx-labs.com/repository/maven-snapshots/`.

To force generation from Nexus instead of an already-installed local archetype, use an empty local repository and the remote archetype catalog:

```bash
mvn -B -ntp archetype:generate \
  -Dmaven.repo.local=/tmp/hex-archetype-generate-check \
  -DarchetypeCatalog=remote \
  -DarchetypeGroupId=com.appx \
  -DarchetypeArtifactId=hexagonal-spring-boot-archetype \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=com.appx \
  -DartifactId=customer-service \
  -Dversion=0.1.0-SNAPSHOT \
  -Dpackage=com.appx.customers \
  -DinteractiveMode=false
```

If Maven mirrors all repositories through `maven-public`, make sure `nexus-snapshots` is excluded from that mirror and active as a snapshot repository.

## Archetype Benefits

- Enforces clear hexagonal boundaries via module separation.
- Standardizes project bootstrapping across teams.
- Provides a reusable Spring Cloud OpenFeign client artifact out of the box.
- Generates OpenAPI docs and Swagger UI for the inbound REST adapter through springdoc-openapi.
- Includes Kubernetes Config Server bootstrap support for the executable `webapp`, which hosts the inbound REST and inbound Kafka adapters.
- Builds a real React/Vite frontend in the generated `webapp` module and serves it from Spring Boot static resources.
- Includes common build quality defaults (unit/integration split, coverage, enforcer rules).
- Includes CI-friendly versioning support (`${revision}`) for consistent module versions.
- Reduces setup time for new services and improves consistency in code reviews.

## Current Limitations

- Defaults are opinionated (Spring MVC + JPA + explicit converter/factory mapping); not all teams need this stack.
- Generated project enforces modern toolchain versions (Maven 3.9.15+ / Java 25).
- Testcontainers integration test is a skeleton (`contextLoads`) and needs real assertions.
- No complete GitOps application scaffolding in generated output yet; `deploy/appx-spring-boot/values.yaml` is a starting point for the AppX shared chart.
- No archetype integration-test module (`src/it`) in this archetype project yet.

## Future Extensions

- Add archetype `src/it` verification projects for generation regression tests.
- Add optional profiles/features (Flyway, OpenAPI, Security).
- Add publishing profiles for internal repos and Maven Central metadata/signing.
- Add Maven Wrapper generation and pinned Maven 3.9.15 version in generated projects.
- Add optional module variants (e.g., reactive adapter, messaging adapter).
- Add architecture rules checks (e.g., ArchUnit) to enforce inward dependencies.

## Notes

- The generated parent POM uses `${revision}` to keep module versions aligned.
- You can override defaults during generation using `-DjavaVersion` and `-DspringBootVersion`.
