# Inbound REST Adapter Module

This module is the executable Spring Boot HTTP service for `${artifactId}`. Requests enter the system here and are translated into application commands.

**Responsibilities**

- Own REST controllers and HTTP route mappings.
- Own HTTP request and response DTOs.
- Own OpenAPI/Swagger annotations that describe REST operations and DTO schemas.
- Own the Spring Boot REST application entry point and HTTP runtime configuration.
- Convert transport DTOs into application commands and domain-facing response values.
- Keep HTTP-specific validation, status codes, and JSON shape out of the application and domain modules.
- Publish integration events to Kafka after local application events when Kafka publishing is enabled.

**Current Contents**

- `CustomerController`: REST endpoints for registering customers and retrieving customer payloads.
- `RegisterCustomerRequest`: HTTP request DTO.
- `RegisterCustomerResponse`: HTTP response DTO.
- `CustomerResponse`: HTTP response DTO for full customer payloads.
- `GatewayUserResponse`: HTTP response DTO for the authenticated gateway actor.
- `CustomerRestConverter`: converts between REST DTOs and application/domain types.
- `GatewayUserMapper`: converts JWT claims into REST-facing user data.
- `RestExceptionHandler`: maps application and validation errors into RFC 9457 `ProblemDetail` responses.
- `InboundRestApplication`: Spring Boot entry point for the HTTP service.
- `ApplicationWiringConfig`: explicit application-service wiring for the REST runtime.
- `SecurityConfig`: resource-server security for tokens relayed by `spring-gateway-base`.
- `OpenApiConfig`: Swagger/OpenAPI metadata and OAuth2 login configuration.
- `CustomerRegisteredKafkaPublisher`: publishes the customer registration integration event to Kafka when enabled.
- `springdoc-openapi-starter-webmvc-ui`: generates `/v3/api-docs` and Swagger UI from Spring MVC mappings and OpenAPI annotations.
- `CustomerControllerTest`: AssertJ-based adapter test for controller/use-case delegation and gateway JWT claim mapping.
- `RestExceptionHandlerTest`: AssertJ-based test for REST error response mapping.
- `CustomerRegistrationIT`: Spring Boot integration test using Testcontainers PostgreSQL.

**Published HTTP Contract**

- `POST /api/v1/customers`: registers a customer and returns `RegisterCustomerResponse`.
- `GET /api/v1/customers/{customerId}`: returns `CustomerResponse` for service-to-service lookup through the generated client.
- `GET /api/v1/customers/me`: returns authenticated gateway user claims.

**Dependency Direction**

- This module may depend on `application` and outbound adapters needed by the REST runtime.
- This module receives `RegisterCustomerUseCase` and `GetCustomerUseCase` from the application layer.
- This module must not depend on `webapp`, `client`, or `adapters/inbound-kafka`.
- REST controllers must not initiate service-to-service HTTP calls.
- Do not put REST-to-REST fanout behind application outbound ports for request/response flows. Browser-facing workflows that need multiple services belong in `spring-gateway-base` composition endpoints.
- Event-driven cross-service calls are allowed from `adapters/inbound-kafka` through the generated client, with those HTTP edges covered by WireMock integration tests.

**Where To Make Changes**

- Add new REST endpoints here when exposing existing or new use cases over HTTP.
- Add REST-specific DTOs here when the wire format differs from application commands.
- Add converter logic here when translating HTTP data into application commands.
- Add `@Operation`, `@ApiResponse`, `@Tag`, and `@Schema` annotations here when changing API contracts.
- Add or update `RestExceptionHandler` mappings when new application exceptions need stable HTTP status codes and problem types.
- Add adapter tests here when controller behavior, DTO mapping, validation, or JWT claim handling changes. Use AssertJ assertions.

**OpenAPI**

This module uses `springdoc-openapi-starter-webmvc-ui` because the generated service uses Spring MVC through `spring-boot-starter-web`.

Generated applications expose:

- JSON OpenAPI document: `/v3/api-docs`
- YAML OpenAPI document: `/v3/api-docs.yaml`
- Swagger UI: `/swagger-ui.html`

For Kubernetes, deployment-specific REST settings are supplied through Spring Cloud Config Client in this module. Keep runtime values such as CORS, servlet settings, actuator exposure, issuer URI, Swagger OAuth client, datasource URL, and public URLs in Config Server under `services/${artifactId}/${artifactId}-kubernetes.yml`.

**Local Development**

From the generated project root:

```bash
mvn -pl adapters/inbound-rest -am spring-boot:run -Dspring-boot.run.profiles=dev
```

The `dev` profile uses the root `compose.yaml` through Spring Boot Docker Compose support, starts PostgreSQL and Kafka if needed, and enables the Kafka publisher example.

**Gateway User**

`spring-gateway-base` relays the authenticated actor or impersonated user as a Bearer token. REST endpoints can read the current user with:

```java
@AuthenticationPrincipal Jwt jwt
```

Use `GatewayUserMapper` when an endpoint needs transport-safe user details from common JWT claims such as `sub`, `preferred_username`, `email`, and `name`.

**Avoid**

- Do not implement business rules here; call application use cases.
- Do not inject JPA repositories or external clients directly into controllers.
- Do not call generated OpenFeign clients from this module. Use cases exposed by REST should stay local to this service.
- Do not return JPA entities or domain objects directly if the API contract needs stable DTOs.
- Do not let generic exceptions define public API behavior. Translate typed application exceptions into `ProblemDetail` responses here.
- Do not add Feign, WireMock, or other service-client dependencies here. REST adapter tests should verify request mapping, validation, auth claim mapping, and error responses.
