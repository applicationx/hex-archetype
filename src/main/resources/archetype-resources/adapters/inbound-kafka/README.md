# ${artifactId} Inbound Kafka Adapter

**Purpose**

This module is the executable Spring Boot Kafka listener service for `${artifactId}`. It receives Kafka records and invokes application-facing behavior.

**Owns**

- Kafka listener methods with listener-specific annotations.
- Kafka message DTOs that represent the wire payload consumed from Kafka.
- Converters from Kafka message DTOs into domain-facing identifiers or application commands.
- Spring Boot startup and Kafka listener runtime configuration for this service.
- Testcontainers-based Kafka smoke tests for listener wiring.
- WireMock-backed tests when Kafka-triggered workflows call other services through the generated OpenFeign client.

**Does Not Own**

- Domain model or business rules. Put those in `domain`.
- Use case interfaces or commands. Put those in `application`.
- Persistence, repositories, or database mapping. Put those in `adapters/outbound-jpa`.
- Browser frontend code. Put that in `webapp`.

**Dependency Direction**

`adapters/inbound-kafka` depends on `application` and `client`. The generated example consumes a `CustomerRegisteredKafkaMessage` from `customer-registered-events`, extracts the customer id, and uses `MyAppClient` to fetch the full customer payload over HTTP.

Never make `application` depend on this module. The application layer must not know Kafka exists.

Kafka-triggered workflows may call other services. Keep the listener focused on transport concerns: consume the message, convert it to a typed value, and call a client or application port. When calling HTTP services, use the generated client module instead of duplicating paths or DTOs.

**Generated Event Flow**

1. `CustomerApplicationService.register` publishes `CustomerRegistered` through the application `DomainEventPublisher`.
2. `adapters/inbound-rest` adapts `DomainEventPublisher` to Spring's `ApplicationEventPublisher`.
3. `CustomerRegisteredKafkaPublisher` in `adapters/inbound-rest` listens for the Spring event and sends `CustomerRegisteredKafkaMessage` to `customer-registered-events` when `customer.registration.kafka.enabled=true`.
4. `CustomerRegistrationKafkaListener` consumes the Kafka event and calls `MyAppClient#getCustomer(customerId)`.
5. `CustomerRegistrationKafkaListenerIT` verifies the flow with Testcontainers Kafka and WireMock.

**Runtime Composition**

This module is a standalone Spring Boot process. It is not loaded by `webapp` or by `adapters/inbound-rest`.

Kafka listener startup is disabled by default through `customer.registration.kafka.enabled=false` in `adapters/inbound-kafka/src/main/resources/application.yml`. Enable it in a runtime profile or deployment configuration when a Kafka broker is available.

For Kubernetes, put Kafka runtime values in Config Server under `services/${artifactId}/${artifactId}-kubernetes.yml`. Typical keys are:

```yaml
spring:
  kafka:
    bootstrap-servers: kafka.kafka.svc.cluster.local:9092
customer:
  registration:
    topic: customer-registered-events
    kafka:
      enabled: true
```

**Change Guidance For AI Agents**

- Add a new listener here when the trigger is a Kafka topic.
- Keep listener methods thin: validate transport assumptions, convert to a typed value, call a client or use case.
- Keep message records transport-shaped. Do not reuse JPA entities, REST request DTOs, or domain objects as Kafka payloads.
- Add retry, error-handler, dead-letter, and concurrency configuration in this module unless it belongs in Config Server.
- Add a Testcontainers Kafka adapter test when listener topics, message DTOs, deserialization settings, or command conversion changes. Use AssertJ assertions.
- Add WireMock-backed integration tests when a Kafka-triggered use case calls another HTTP service. Stub the remote service and assert the request path, method, headers, payload, and client-visible error behavior.
