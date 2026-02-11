# ${artifactId}

Hexagonal Spring Boot service generated from the `hexagonal-spring-boot-archetype` archetype.

## Modules

- `domain`: domain model and rules.
- `application`: use cases and ports.
- `adapters/inbound-rest`: REST adapter.
- `adapters/outbound-jpa`: JPA adapter.
- `webapp`: Spring Boot composition root.
- `client`: reusable HTTP client module.

## Requirements

- Java ${javaVersion}
- Maven 4+

## Build

```bash
mvn -B -ntp verify
```

## Run app

```bash
mvn -pl webapp -am spring-boot:run
```
