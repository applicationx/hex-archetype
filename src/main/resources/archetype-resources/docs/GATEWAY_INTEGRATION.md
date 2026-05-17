#set($d = '$')
# Gateway Integration

This guide is for an AI agent integrating this generated service with `/home/appx/github/spring-gateway-base`.

The generated service is designed to run behind `spring-gateway-base`. The gateway owns browser login, logout, Redis-backed session state, actor token relay, impersonation token relay, and browser-facing HTTP composition across services. This service should stay a downstream resource server and should not implement its own gateway login/session behavior.

AppX service communication rule: REST services must not initiate REST-to-REST calls. Browser-facing HTTP workflows that need data from multiple services belong in `spring-gateway-base` as gateway composition endpoints. Event-driven workflows may use `inbound-kafka -> generated client -> target REST service`, with those HTTP calls covered by WireMock tests.

**Generated Service Facts**

- Service repository: `applicationx/${artifactId}` unless the generated repo was renamed.
- Kubernetes service name: `${artifactId}`.
- Default Kubernetes namespace from the AppX dev deployment guide: `gateway-system`.
- Internal service URL for gateway routing: `http://${artifactId}.gateway-system.svc.cluster.local:8080`.
- Gateway path prefix to add: choose a stable short prefix, for example `/${artifactId}` or a domain-specific prefix such as `/customers`.
- Downstream REST API currently starts at `/api/v1/customers`.
- Swagger UI path inside the service: `/swagger-ui.html`.
- OpenAPI JSON path inside the service: `/v3/api-docs`.
- The service expects `Authorization: Bearer ...` from the gateway and maps user data from the JWT in `adapters/inbound-rest`.

**spring-gateway-base Files To Inspect**

Open these files in `/home/appx/github/spring-gateway-base` before editing:

- `src/main/resources/application.yaml`: Spring Cloud Gateway route definitions and downstream service URI environment defaults.
- `src/main/java/com/appx/gateway/config/SecurityConfig.java`: authenticated path rules, OAuth2 login, resource-server setup, and logout.
- `src/main/java/com/appx/gateway/config/ActorOrImpersonationRelayFilter.java`: allow-list for paths that receive the actor or active impersonation bearer token.
- `src/main/java/com/appx/gateway/web/`: gateway-owned HTTP controllers for composition/admin endpoints.
- `src/main/java/com/appx/gateway/impersonation/UserDirectoryClient.java`: current reactive `WebClient` downstream-call pattern for gateway-owned service calls.
- `src/test/java/com/appx/gateway/config/EanGatewayRouteConfigurationTest.java`: current AssertJ route-config test pattern.
- `helm/spring-gateway-base/values.yaml`: Kubernetes environment variables for downstream service URLs.
- `/home/appx/github/k3s-dev/manifests/argocd/spring-gateway-base-application.yaml`: confirms Argo CD consumes `helm/spring-gateway-base/values.yaml`.

**Gateway Role**

Use two gateway patterns deliberately:

- Simple proxy route: use Spring Cloud Gateway route config when the browser path maps to one downstream service.
- Composition endpoint: add a controller/service in `spring-gateway-base` when one browser endpoint must read or combine data from multiple downstream services.

Do not put multi-service REST orchestration inside generated REST services. If service A needs to react to service B, model that as a domain event consumed by `adapters/inbound-kafka`, and let the Kafka adapter use a generated client for the allowed `inbound-kafka -> REST` call.

For gateway composition endpoints, prefer the existing reactive `WebClient` pattern used by `UserDirectoryClient`. Do not use the generated OpenFeign client inside `spring-gateway-base` unless the gateway has been intentionally adapted for blocking clients, because `spring-gateway-base` is WebFlux/reactive.

**Route To Add**

Add a route in `spring-gateway-base/src/main/resources/application.yaml` under:

```yaml
spring:
  cloud:
    gateway:
      server:
        webflux:
          routes:
```

Template:

```yaml
- id: ${artifactId}
  uri: ${d}{REPLACE_WITH_SERVICE_URI_ENV:http://${artifactId}.gateway-system.svc.cluster.local:8080}
  predicates:
    - Path=/${artifactId}/**
  filters:
    - RewritePath=/${artifactId}/?(?<segment>.*), /${d}\{segment}
```

If the public gateway path should not be `/${artifactId}`, choose the final prefix first and use it consistently in the route, security rule, token relay filter, tests, and frontend calls.

**Security Rule**

In `SecurityConfig.java`, add the selected gateway path prefix as authenticated:

```java
.pathMatchers("/${artifactId}/**").authenticated()
```

Keep actuator health/info/prometheus public. Do not make the generated service's business endpoints public through the gateway unless the product explicitly requires anonymous access.

**Token Relay**

In `ActorOrImpersonationRelayFilter.java`, update `shouldRelayPath` so the selected gateway path receives the actor token or active impersonation token:

```java
|| path.equals("/${artifactId}")
|| path.startsWith("/${artifactId}/")
```

This is required for downstream REST controllers to see `Authorization: Bearer ...`. Without this change, the gateway may authenticate the browser session but the generated service will not receive a bearer token for `@AuthenticationPrincipal Jwt`.

Gateway composition endpoints that call downstream services must also relay the actor or active impersonation token on their `WebClient` calls. Reuse or extract the existing gateway token lookup behavior instead of making downstream service calls without an `Authorization` header.

**Composition Endpoint Guidance**

Use this pattern when a frontend endpoint spans multiple services:

1. Add a controller under `spring-gateway-base/src/main/java/com/appx/gateway/web/`.
2. Add one reactive downstream client per service integration, following the `UserDirectoryClient` `WebClient` style.
3. Read the actor or impersonation token from gateway session state and set `Authorization: Bearer ...` on each downstream request.
4. Combine the downstream responses into a gateway-owned response DTO.
5. Keep writes explicit. If a workflow changes multiple services, prefer a command to one owning service plus domain events, not a gateway transaction across services.
6. Test downstream HTTP edges with WireMock and AssertJ.

Example endpoint shape:

```text
GET /workspace/summary
  -> gateway reads current actor token
  -> gateway calls /customers/api/v1/customers/me
  -> gateway calls /orders/api/v1/orders/recent
  -> gateway returns one browser-facing summary DTO
```

This keeps REST composition at the edge and preserves the rule that generated REST services do not call other REST services.

**Gateway Helm Values**

In `spring-gateway-base/helm/spring-gateway-base/values.yaml`, add an environment variable for the service URI:

```yaml
env:
  - name: REPLACE_WITH_SERVICE_URI_ENV
    value: "http://${artifactId}.gateway-system.svc.cluster.local:8080"
```

Keep this value in the gateway repo because Argo CD deploys `spring-gateway-base` from that repo's Helm values. Before committing, replace `REPLACE_WITH_SERVICE_URI_ENV` with a clean shell-style name such as `CUSTOMER_SERVICE_URI`.

**Tests To Add Or Update**

Add a route configuration test beside `EanGatewayRouteConfigurationTest.java` or extend the current test class.

Recommended assertions:

- route id is `${artifactId}`;
- route URI is `${d}{REPLACE_WITH_SERVICE_URI_ENV:http://${artifactId}.gateway-system.svc.cluster.local:8080}` with the chosen env var name;
- route predicate is `Path=/${artifactId}/**`;
- route rewrite matches the `RewritePath` filter shown above and preserves the captured `segment` value;
- `SecurityConfig` requires authentication for `/${artifactId}/**`;
- `ActorOrImpersonationRelayFilter.shouldRelayPath("/${artifactId}/api/v1/customers")` returns `true`.

Use AssertJ assertions, matching the existing gateway test style.

**Frontend Integration**

If `/home/appx/github/appx-web` should call this service through the gateway:

- Add a typed API helper under `appx-web/src/lib/` or the closest existing feature-specific client.
- Call the gateway path, not the Kubernetes service URL.
- Use relative browser paths or the existing gateway base helper pattern, depending on where the call is made.
- Do not call the generated service directly from the browser.
- If a screen needs data from multiple services, call one gateway composition endpoint instead of making the browser coordinate several downstream service routes.

Example browser path after gateway integration:

```text
/${artifactId}/api/v1/customers
```

**Swagger And OpenAPI Through Gateway**

The generated service has local Swagger UI and OpenAPI endpoints:

```text
/${artifactId}/swagger-ui.html
/${artifactId}/v3/api-docs
```

If Swagger UI should work through the gateway, verify:

- the gateway route rewrites `/${artifactId}/...` to `/...`;
- the browser is authenticated at the gateway before loading Swagger UI;
- `Authorization: Bearer ...` is relayed for API calls;
- the service's Swagger OAuth client id is configured with `SWAGGER_UI_OAUTH_CLIENT_ID`;
- the ZITADEL redirect URI matches the public Swagger UI OAuth redirect path if direct Swagger OAuth login is enabled.

For normal AppX usage, prefer gateway login first and let the gateway relay the bearer token to the service.

**Verification**

After editing `spring-gateway-base`:

```bash
cd /home/appx/github/spring-gateway-base
mvn -B -ntp test
```

If deployed to k3s dev:

```bash
kubectl get application spring-gateway-base -n argocd
kubectl get pods -n gateway-system -l app.kubernetes.io/instance=spring-gateway-base
```

Smoke check through the gateway after authentication:

```text
https://gateway.appx-cloud.com/${artifactId}/api/v1/customers/me
https://gateway.appx-cloud.com/${artifactId}/v3/api-docs
```

The expected outcome is that the gateway authenticates the user, rewrites `/${artifactId}/...` to the generated service's native path, relays the actor or impersonation bearer token, and the generated service sees the JWT in its REST adapter.
