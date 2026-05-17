# Inbound REST Adapter Module

This module exposes application use cases over HTTP. It is an inbound adapter: requests enter the system here and are translated into application commands.

**Responsibilities**

- Own REST controllers and HTTP route mappings.
- Own HTTP request and response DTOs.
- Own OpenAPI/Swagger annotations that describe REST operations and DTO schemas.
- Convert transport DTOs into application commands and domain-facing response values.
- Keep HTTP-specific validation, status codes, and JSON shape out of the application and domain modules.

**Current Contents**

- `CustomerController`: REST endpoints for registering customers and retrieving customer payloads.
- `RegisterCustomerRequest`: HTTP request DTO.
- `RegisterCustomerResponse`: HTTP response DTO.
- `CustomerResponse`: HTTP response DTO for full customer payloads.
- `GatewayUserResponse`: HTTP response DTO for the authenticated gateway actor.
- `CustomerRestConverter`: converts between REST DTOs and application/domain types.
- `GatewayUserMapper`: converts JWT claims into REST-facing user data.
- `RestExceptionHandler`: maps application and validation errors into RFC 9457 `ProblemDetail` responses.
- `springdoc-openapi-starter-webmvc-ui`: generates `/v3/api-docs` and Swagger UI from Spring MVC mappings and OpenAPI annotations.
- `CustomerControllerTest`: AssertJ-based adapter test for controller/use-case delegation and gateway JWT claim mapping.
- `RestExceptionHandlerTest`: AssertJ-based test for REST error response mapping.

**Dependency Direction**

- This module may depend on `application`.
- This module receives `RegisterCustomerUseCase` and `GetCustomerUseCase` from the application layer.
- This module must not depend on `webapp`, `client`, or outbound adapter modules.
- REST controllers must not initiate service-to-service HTTP calls. Put cross-service dependencies behind application outbound ports and implement them in outbound adapters.

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

For Kubernetes, deployment-specific REST settings are supplied through Spring Cloud Config Client in `webapp`. Keep REST endpoint code here, but put runtime values such as CORS, servlet settings, actuator exposure, or public URLs in Config Server under `services/${artifactId}/${artifactId}-kubernetes.yml`.

**Gateway User**

`spring-gateway-base` relays the authenticated actor or impersonated user as a Bearer token. REST endpoints can read the current user with:

```java
@AuthenticationPrincipal Jwt jwt
```

Use `GatewayUserMapper` when an endpoint needs transport-safe user details from common JWT claims such as `sub`, `preferred_username`, `email`, and `name`.

**Avoid**

- Do not implement business rules here; call application use cases.
- Do not inject JPA repositories or external clients directly into controllers.
- Do not return JPA entities or domain objects directly if the API contract needs stable DTOs.
- Do not let generic exceptions define public API behavior. Translate typed application exceptions into `ProblemDetail` responses here.
- Do not add Feign, WireMock, or other service-client dependencies here. REST adapter tests should verify request mapping, validation, auth claim mapping, and error responses.
