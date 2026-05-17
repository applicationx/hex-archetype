# Webapp Module

This module is the Spring Boot composition root and browser frontend for `${artifactId}`. It assembles the application, adapters, runtime configuration, and a React/Vite frontend into an executable service.

**Responsibilities**

- Own the Spring Boot main class.
- Own the React/Vite frontend under `src/main/frontend`.
- Wire beans that connect application ports to adapter implementations.
- Adapt application event-publication ports to Spring's `ApplicationEventPublisher`.
- Configure component scanning, JPA repository scanning, and entity scanning.
- Enable OpenFeign client scanning for the generated client module.
- Own runtime configuration files such as `application.yml`.
- Own end-to-end or integration tests that start the Spring application context.

**Current Contents**

- `MyAppApplication`: Spring Boot entry point with component scanning, JPA scanning, entity scanning, and Feign client scanning.
- `ApplicationWiringConfig`: explicit application-service wiring.
- `application.yml`: local runtime defaults.
- `src/main/frontend`: React/Vite app that builds to Spring Boot static resources.
- `CustomerRegistrationIT`: Spring Boot integration test using Testcontainers PostgreSQL.
- Spring Cloud Config Client dependency for Kubernetes runtime configuration.
- Spring Security resource-server configuration for tokens relayed by `spring-gateway-base`.
- OpenAPI OAuth2 configuration for Swagger UI login.

**Dependency Direction**

- This module may depend on `application`, inbound adapters, and outbound adapters.
- This module may depend on `client` when generated clients are part of runtime composition.
- This module should not contain domain rules or transport DTOs.
- This module is allowed to know about multiple adapters because it is the composition root.

**Where To Make Changes**

- Add runtime configuration here when it affects how the service starts.
- Add Spring scanning annotations here when new adapter packages need discovery.
- Add wiring beans here when connecting application ports to adapter implementations.
- Add Spring event listeners here when they are infrastructure reactions to domain events.
- Add integration tests here when verification needs Spring Boot, HTTP, or a real database.
- Add browser UI changes under `src/main/frontend/src`.
- Add API calls in frontend code through a small typed client, not directly inside presentation components.

**Avoid**

- Do not put business logic in the boot application class or configuration classes.
- Do not put REST endpoint logic here; use `adapters/inbound-rest`.
- Do not put persistence implementation here; use `adapters/outbound-jpa`.
- Do not put reusable HTTP client code here; use `client`.

**Domain Events**

The application module publishes domain events through its `DomainEventPublisher` outbound port. This module adapts that port to Spring's `ApplicationEventPublisher`.

`CustomerRegisteredKafkaPublisher` in `adapters/inbound-kafka` is an infrastructure reaction to the Spring event. It forwards `CustomerRegistered` to Kafka only when `customer.registration.kafka.enabled=true`.

For listeners that should run only after a database transaction commits, prefer Spring's `@TransactionalEventListener` in this module or in an adapter module.

**Kubernetes Config Server**

This module owns Spring Cloud Config Client startup because it is the executable service. In Kubernetes, set:

```yaml
SPRING_PROFILES_ACTIVE: kubernetes
CONFIG_SERVER_URL: http://config-server.config-system.svc.cluster.local:8888
```

Runtime values for REST and Kafka adapters should come from Config Server. Keep adapter modules focused on transport code; put broker URLs, topic names, listener enablement, and deployment-specific HTTP settings in `applicationx/spring-config` and Vault.

**Gateway Security**

`spring-gateway-base` authenticates browser users with ZITADEL and relays the actor or impersonated user access token to downstream services in the `Authorization` header. This module validates that token through Spring Security resource-server support.

Local defaults use `ZITADEL_ISSUER_URI=https://auth.appx-labs.com`. In Kubernetes, keep issuer and Swagger OAuth client values in Config Server or deployment environment:

```yaml
ZITADEL_ISSUER_URI: https://auth.appx-labs.com
SWAGGER_UI_OAUTH_CLIENT_ID: replace-me
```

Swagger UI login requires a ZITADEL client with redirect URI:

```text
https://<service-host>/swagger-ui/oauth2-redirect.html
```

**Frontend Build**

Maven installs Node, runs `npm install`, runs `npm run build`, and copies `src/main/frontend/dist` into `target/classes/static`.

For frontend-only development:

```bash
cd webapp/src/main/frontend
npm install
npm run dev
```
