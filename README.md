# Hexagonal Spring Boot Maven Archetype

This repository provides a Maven archetype that generates a multi-module hexagonal architecture project for Spring Boot.

Default generated stack:
- Spring Boot: `4.0.2`
- Java: `25`
- Maven project model: `4.0.0`
- Modules: `domain`, `application`, `adapters/inbound-rest`, `adapters/outbound-jpa`, `webapp`, `client`

## Prerequisites

- Maven `4.x` installed
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
  -DarchetypeGroupId=com.wdj176.archetypes \
  -DarchetypeArtifactId=hexagonal-spring-boot-archetype \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=com.acme \
  -DartifactId=customer-service \
  -Dversion=0.1.0-SNAPSHOT \
  -Dpackage=com.acme.customers \
  -DjavaVersion=25 \
  -DspringBootVersion=4.0.2 \
  -DinteractiveMode=false
```

## Build Generated Project

```bash
cd customer-service
mvn -B -ntp verify
```

## Archetype Benefits

- Enforces clear hexagonal boundaries via module separation.
- Standardizes project bootstrapping across teams.
- Provides a reusable client artifact out of the box.
- Includes common build quality defaults (unit/integration split, coverage, enforcer rules).
- Includes CI-friendly versioning support (`${revision}`) for consistent module versions.
- Reduces setup time for new services and improves consistency in code reviews.

## Current Limitations

- Defaults are opinionated (Spring MVC + JPA + MapStruct); not all teams need this stack.
- Generated project enforces modern toolchain versions (Maven 4 / Java 25).
- Testcontainers integration test is a skeleton (`contextLoads`) and needs real assertions.
- No built-in deployment profile (Nexus/Artifactory/Central) in generated output yet.
- No archetype integration-test module (`src/it`) in this archetype project yet.

## Future Extensions

- Add archetype `src/it` verification projects for generation regression tests.
- Add optional profiles/features (Flyway, PostgreSQL driver, Kafka, OpenAPI, Security).
- Add publishing profiles for internal repos and Maven Central metadata/signing.
- Add Maven Wrapper generation and pinned Maven 4 version in generated projects.
- Add optional module variants (e.g., reactive adapter, messaging adapter).
- Add architecture rules checks (e.g., ArchUnit) to enforce inward dependencies.

## Notes

- The generated parent POM uses `${revision}` to keep module versions aligned.
- You can override defaults during generation using `-DjavaVersion` and `-DspringBootVersion`.
