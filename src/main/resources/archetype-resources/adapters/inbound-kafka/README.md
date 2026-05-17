# ${artifactId} Inbound Kafka Adapter

**Purpose**

This module receives Kafka records and invokes application use cases. It is an inbound adapter, so it adapts an external transport into the application boundary.

**Owns**

- Kafka listener methods and listener-specific annotations.
- Kafka message DTOs that represent the wire payload consumed from Kafka.
- Converters from Kafka message DTOs into application commands.
- Testcontainers-based Kafka smoke tests for listener wiring.

**Does Not Own**

- Domain model or business rules. Put those in `domain`.
- Use case interfaces or commands. Put those in `application`.
- Persistence, repositories, or database mapping. Put those in `adapters/outbound-jpa`.
- Spring Boot application startup and shared configuration defaults. Put those in `webapp`.

**Dependency Direction**

`adapters/inbound-kafka` depends on `application` and calls inbound ports such as `RegisterCustomerUseCase`.

Never make `application` depend on this module. The application layer must not know Kafka exists.

**Runtime Composition**

`webapp` depends on this module so Spring component scanning can discover the listener.

Kafka listener startup is disabled by default through `customer.registration.kafka.enabled=false` in `webapp/src/main/resources/application.yml`. Enable it in a runtime profile or deployment configuration when a Kafka broker is available.

For Kubernetes, put Kafka runtime values in Config Server under `services/${artifactId}/${artifactId}-kubernetes.yml`. Typical keys are:

```yaml
spring:
  kafka:
    bootstrap-servers: kafka.kafka.svc.cluster.local:9092
customer:
  registration:
    kafka:
      enabled: true
```

**Change Guidance For AI Agents**

- Add a new listener here when the trigger is a Kafka topic.
- Keep listener methods thin: validate transport assumptions, convert to an application command, call a use case.
- Keep message records transport-shaped. Do not reuse JPA entities, REST request DTOs, or domain objects as Kafka payloads.
- Add retry, error-handler, dead-letter, and concurrency configuration in `webapp` unless it is specific to one listener.
- Add a Testcontainers Kafka adapter test when listener topics, message DTOs, deserialization settings, or command conversion changes. Use AssertJ assertions.
