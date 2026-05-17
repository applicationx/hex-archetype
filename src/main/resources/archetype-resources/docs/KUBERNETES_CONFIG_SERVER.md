#set($d = '$')
# Kubernetes Config Server

This project is ready to consume the AppX Spring Cloud Config Server when the generated Spring Boot services run in Kubernetes.

The executable Java services are separate modules:

- `adapters/inbound-rest` provides Spring MVC controllers, OpenAPI, security, persistence, and the Kafka publisher for registration events.
- `adapters/inbound-kafka` provides Spring Kafka listeners and allowed event-driven client calls.
- `webapp` is the Node/Vite browser frontend and does not consume Spring Cloud Config.

## Runtime Config Server

The AppX Config Server runs inside the k3s cluster:

```text
Namespace: config-system
Service:   config-server
URL:       http://config-server.config-system.svc.cluster.local:8888
```

The generated service `application.yml` files contain bootstrap-level config for Kubernetes:

```yaml
spring:
  application:
    name: ${artifactId}
  profiles:
    active: ${d}{SPRING_PROFILES_ACTIVE:local}
  config:
    import: ${d}{SPRING_CONFIG_IMPORT:optional:configserver:${d}{CONFIG_SERVER_URL:http://config-server.config-system.svc.cluster.local:8888}}
```

Use `SPRING_PROFILES_ACTIVE=kubernetes` in Kubernetes. Keep the `optional:` prefix for local development and smoke tests where Config Server might not be reachable.

## Kubernetes Environment

Set these environment variables on the Deployment:

```yaml
env:
  - name: BPL_JVM_THREAD_COUNT
    value: "50"
  - name: SPRING_PROFILES_ACTIVE
    value: "kubernetes"
  - name: CONFIG_SERVER_URL
    value: "http://config-server.config-system.svc.cluster.local:8888"
  - name: ZITADEL_ISSUER_URI
    value: "https://auth.appx-labs.com"
  - name: SWAGGER_UI_OAUTH_CLIENT_ID
    value: "replace-me"
```

The generated `deploy/appx-spring-boot/values.yaml` follows the same pattern used by the AppX shared Spring Boot chart.

## Config Repository Files

Put non-secret config in the Config Server Git repository using the service names:

```text
services/${artifactId}/${artifactId}.yml
services/${artifactId}/${artifactId}-kubernetes.yml
services/${artifactId}-inbound-kafka/${artifactId}-inbound-kafka.yml
services/${artifactId}-inbound-kafka/${artifactId}-inbound-kafka-kubernetes.yml
```

Example `services/${artifactId}/${artifactId}-kubernetes.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgresql.database.svc.cluster.local:5432/${artifactId}
  kafka:
    bootstrap-servers: kafka.kafka.svc.cluster.local:9092
    consumer:
      value-deserializer: org.springframework.kafka.support.serializer.JacksonJsonDeserializer
    producer:
      value-serializer: org.springframework.kafka.support.serializer.JacksonJsonSerializer

customer:
  registration:
    topic: customer-registered-events
    kafka:
      enabled: true

springdoc:
  swagger-ui:
    oauth:
      client-id: ${d}{SWAGGER_UI_OAUTH_CLIENT_ID}
```

Do not put passwords, tokens, private keys, or API secrets in Git.

## Vault Values

Put sensitive values in Vault under:

```text
secret/config/${artifactId}/kubernetes
```

Example:

```bash
kubectl -n config-system exec vault-0 -- vault kv put secret/config/${artifactId}/kubernetes \
  spring.datasource.username="${artifactId}" \
  spring.datasource.password="change-me"
```

## Adapter Guidance

REST endpoint behavior and REST runtime ownership belong in `adapters/inbound-rest`; deployment-specific REST runtime properties should still come from Config Server.

Kafka listener behavior and Kafka service startup belong in `adapters/inbound-kafka`; broker URLs, group IDs, topic names, concurrency, retry, and listener enablement belong in Config Server.

Gateway user handling is implemented as JWT resource-server support in `adapters/inbound-rest`. The `spring-gateway-base` gateway relays the actor or impersonated user token as the `Authorization: Bearer ...` header. REST controllers can use `@AuthenticationPrincipal Jwt` and map claims into transport DTOs.

Swagger UI uses OAuth2 authorization-code login with PKCE. Configure `SWAGGER_UI_OAUTH_CLIENT_ID` with a ZITADEL browser/web client that has a redirect URI matching the service Swagger UI OAuth redirect endpoint, normally:

```text
https://<service-host>/swagger-ui/oauth2-redirect.html
```

## Smoke Checks

Check Config Server output from inside the cluster:

```bash
kubectl -n config-system run ${artifactId}-config-check --rm -i --restart=Never --image=curlimages/curl -- \
  curl -fsS http://config-server:8888/${artifactId}/kubernetes
```

Port-forward for local inspection:

```bash
kubectl -n config-system port-forward svc/config-server 8888:8888
curl -fsS http://localhost:8888/${artifactId}/kubernetes
```
