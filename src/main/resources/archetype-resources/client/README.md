# Client Module

This module contains reusable client-side API types for calling `${artifactId}` from another Spring application.

**Responsibilities**

- Own the Spring Cloud OpenFeign client interface.
- Own client request and response DTOs.
- Keep generated service consumers from duplicating HTTP paths and DTO shapes.
- Provide a small dependency that downstream services can include when they need to call this service.

**Current Contents**

- `MyAppClient`: OpenFeign interface for the service HTTP API.
- `RegisterCustomerRequest`: client request DTO.
- `RegisterCustomerResponse`: client response DTO.

**Dependency Direction**

- This module should be usable by other applications.
- It should not depend on `webapp`, `application`, `domain`, or adapter modules.
- It may depend on Spring Cloud OpenFeign and resilience libraries needed by the client interface.

**Runtime Use**

In a consuming Spring Boot application, enable Feign scanning for this client:

```java
@EnableFeignClients(basePackageClasses = MyAppClient.class)
```

Configure the target service URL:

```yaml
${artifactId}:
  client:
    url: http://localhost:8080
```

Spring Cloud OpenFeign can wrap methods with Spring Cloud CircuitBreaker when enabled by the consuming application. With `resilience4j-bulkhead` on the classpath, Spring Cloud CircuitBreaker can also apply Resilience4j bulkheads.

**Where To Make Changes**

- Add or update client DTOs here when the public HTTP API changes.
- Add Feign methods here for new public endpoints that other services should call.
- Keep paths and payloads aligned with `adapters/inbound-rest`.
- Use WireMock-backed tests when changing paths, payloads, headers, status handling, or error mapping for this client.

**Avoid**

- Do not put server-side controller code here.
- Do not depend on server implementation modules.
- Do not put domain or persistence objects in the client contract.
