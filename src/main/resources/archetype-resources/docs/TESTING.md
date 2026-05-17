# Testing Guide

This project uses a layered test strategy that matches the generated hexagonal module boundaries.

## Commands

Run the full generated-project verification with:

```bash
mvn -B -ntp verify
```

Do not stop at `mvn integration-test`; Maven Failsafe reports integration-test failures during the `verify` phase.

## Naming

- `*Test.java`: fast unit and adapter tests run by Maven Surefire.
- `*IT.java`: integration tests run by Maven Failsafe.

## Assertion Style

Use AssertJ assertions:

```java
assertThat(result).isEqualTo(expected);
```

Avoid JUnit assertion methods in generated tests unless a specific JUnit API is needed for assumptions or lifecycle control.

## Module Guidance

- `domain`: pure unit tests for domain value objects and entities.
- `application`: unit tests for use-case orchestration using in-memory fakes for outbound ports.
- `adapters/inbound-rest`: controller, DTO mapping, gateway JWT claim mapping, and `ProblemDetail` error mapping tests.
- `adapters/inbound-kafka`: Testcontainers Kafka listener tests for topic, JSON deserialization, and command conversion.
- `webapp`: full Spring Boot integration tests that verify module composition, OpenAPI output, security, and persistence.

## Testcontainers

The generated integration tests use Testcontainers where the behavior depends on real infrastructure:

- PostgreSQL in `webapp` integration tests.
- Kafka in `adapters/inbound-kafka` integration tests.

Prefer static `@Container` fields for one container per test class. Do not enable parallel execution for Testcontainers-backed integration tests by default.

When Spring Boot has first-class connection support for a container, prefer `@ServiceConnection`. Use `@DynamicPropertySource` when a test owns custom wiring and needs explicit property registration.

## REST Error Contracts

REST APIs should return RFC 9457 `ProblemDetail` responses for client-visible errors.

- Throw typed application exceptions from the application layer.
- Translate those exceptions to HTTP status, title, type, detail, and optional properties in `adapters/inbound-rest`.
- Keep public problem `type` URIs stable once clients depend on them.

## Kafka Listener Tests

Kafka listener tests should:

- Use a real Kafka container for broker behavior.
- Use a unique consumer group per test class.
- Set `auto.offset.reset=earliest`.
- Send a representative JSON message through `KafkaTemplate`.
- Assert asynchronously by polling or waiting until the listener records the expected command.

Avoid sharing topics across unrelated tests unless the test data is uniquely keyed and isolated.
