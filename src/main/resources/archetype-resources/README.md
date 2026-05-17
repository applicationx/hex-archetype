# ${artifactId}

Hexagonal Spring Boot service generated from the `hexagonal-spring-boot-archetype` archetype.

**Modules**

- `domain`: domain model and rules.
- `application`: use cases and ports.
- `adapters/inbound-rest`: REST adapter.
- `adapters/inbound-kafka`: Kafka command adapter.
- `adapters/outbound-jpa`: JPA adapter.
- `webapp`: Spring Boot composition root with a React/Vite frontend.
- `client`: reusable HTTP client module.
- `docs/KUBERNETES_CONFIG_SERVER.md`: AppX Config Server setup for Kubernetes deployments.
- `docs/TESTING.md`: generated test strategy and integration-test guidance.
- `deploy/appx-spring-boot/values.yaml`: starting values file for the AppX shared Spring Boot Helm chart.

**Requirements**

- Java ${javaVersion}
- Maven 3.9.15+

**Build**

```bash
mvn -B -ntp verify
```

**Run App**

```bash
mvn -pl webapp -am spring-boot:run
```

The `webapp` module builds the React frontend from `webapp/src/main/frontend` and serves the production assets from Spring Boot static resources.

**OpenAPI And Swagger UI**

The `adapters/inbound-rest` module uses `springdoc-openapi-starter-webmvc-ui` and OpenAPI annotations on REST endpoints and DTOs.

When the app is running:

- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- OpenAPI YAML: `http://localhost:8080/v3/api-docs.yaml`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

Swagger UI includes an OAuth2 Authorize button for ZITADEL. Set `SWAGGER_UI_OAUTH_CLIENT_ID` for the browser client used by Swagger UI.

**REST Error Handling**

The REST adapter maps application and validation failures to RFC 9457 `ProblemDetail` responses through `RestExceptionHandler`.

Typed application exceptions belong in `application`; HTTP status codes, problem titles, problem types, and response details belong in `adapters/inbound-rest`.

**OpenFeign Client**

The `client` module provides a Spring Cloud OpenFeign client interface:

```java
@EnableFeignClients(basePackageClasses = MyAppClient.class)
```

Set the target URL with:

```yaml
${artifactId}:
  client:
    url: http://localhost:8080
```

When Spring Cloud CircuitBreaker is enabled in the consuming app, OpenFeign calls are wrapped in circuit breakers. With `resilience4j-bulkhead` on the classpath, Spring Cloud CircuitBreaker also applies Resilience4j bulkheads.

**Kafka Inbound Adapter**

The `adapters/inbound-kafka` module consumes `customer-registered-events` messages from Kafka. The message carries the registered customer id; the listener uses the generated OpenFeign client to fetch the full customer payload from the REST API. Its integration test uses Testcontainers Kafka plus WireMock for the HTTP client edge.

Kafka listener startup is disabled by default so local runs and generated smoke tests do not require a broker. Enable it with:

```yaml
customer:
  registration:
    topic: customer-registered-events
    kafka:
      enabled: true
```

**Kubernetes Config Server**

The executable service is `webapp`; the REST and Kafka modules are adapters loaded by that service. `webapp` includes Spring Cloud Config Client and imports the AppX Config Server by default when deployed with Kubernetes environment variables:

```yaml
env:
  - name: SPRING_PROFILES_ACTIVE
    value: kubernetes
  - name: CONFIG_SERVER_URL
    value: http://config-server.config-system.svc.cluster.local:8888
```

Put non-secret Kubernetes runtime config in `applicationx/spring-config` under `services/${artifactId}/`. Put secrets in Vault under `secret/config/${artifactId}/kubernetes`.

See `docs/KUBERNETES_CONFIG_SERVER.md` for the full setup.

**Testing**

Run the full test suite with `mvn -B -ntp verify`. Unit tests use `*Test.java`; integration tests use `*IT.java` and Maven Failsafe.

See `docs/TESTING.md` for module-specific test placement, Testcontainers guidance, and AssertJ assertion conventions.

For service-to-service HTTP tests, use WireMock by default. REST controllers should not initiate outbound service calls; Kafka-triggered workflows may call other services through application outbound ports and HTTP adapters, and those HTTP edges should be stubbed with WireMock in integration tests.

**Gateway User**

`spring-gateway-base` relays the authenticated actor or impersonated user token to downstream services as `Authorization: Bearer ...`. This service validates that JWT with Spring Security resource-server support. The REST adapter includes `/api/v1/customers/me` as a small example of reading the current gateway user from JWT claims.
