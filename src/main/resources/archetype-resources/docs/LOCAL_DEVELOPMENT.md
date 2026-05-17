#set($d = '$')
# Local Development

This generated service can run locally in two modes:

- `local` profile: default profile, uses in-memory H2 and keeps Kafka disabled.
- `dev` profile: uses `compose.yaml` for PostgreSQL and Kafka, enables Kafka publication/listening, and keeps the Spring Boot services running on the host JVM.

Use `dev` when you want to test the real local stack: inbound REST, OpenAPI/Swagger UI, PostgreSQL persistence, Kafka publication, and Kafka listener behavior.

**Recommended Local Run**

Start the REST service with the `dev` profile:

```bash
mvn -pl adapters/inbound-rest -am spring-boot:run -Dspring-boot.run.profiles=dev
```

With `spring-boot-docker-compose` on the `adapters/inbound-rest` classpath, Spring Boot reads `compose.yaml`, starts PostgreSQL and Kafka if needed, waits for readiness, and creates connection details for the app.

Start the Kafka listener service in a second terminal:

```bash
mvn -pl adapters/inbound-kafka -am spring-boot:run -Dspring-boot.run.profiles=dev
```

The generated `dev` profile uses:

```yaml
spring:
  docker:
    compose:
      file: ${d}{DOCKER_COMPOSE_FILE:../../compose.yaml}
      lifecycle-management: start-only
```

`start-only` keeps PostgreSQL and Kafka running when the app exits. This is useful for repeated local restarts. Stop the services explicitly when finished:

```bash
docker compose down
```

**Manual Compose Mode**

If you want to manage containers yourself:

```bash
docker compose up -d
SPRING_DOCKER_COMPOSE_ENABLED=false mvn -pl adapters/inbound-rest -am spring-boot:run -Dspring-boot.run.profiles=dev
SPRING_DOCKER_COMPOSE_ENABLED=false mvn -pl adapters/inbound-kafka -am spring-boot:run -Dspring-boot.run.profiles=dev
```

Manual mode uses the fixed local ports in `compose.yaml`:

- PostgreSQL: `localhost:15432`
- Kafka: `localhost:19092`

Those non-default ports avoid common conflicts with a locally installed PostgreSQL or Kafka. If you change them in `compose.yaml`, also update `SPRING_DATASOURCE_URL` and `SPRING_KAFKA_BOOTSTRAP_SERVERS` when running manual mode.

**OpenAPI**

After the app starts:

```text
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
http://localhost:8080/v3/api-docs.yaml
```

Swagger UI is public so local API contracts can be inspected without a token. Business endpoints remain protected by the resource-server security configuration unless you provide a valid JWT.

**Kafka Smoke Flow**

In the `dev` profile:

1. `POST /api/v1/customers` registers a customer.
2. The application publishes `CustomerRegistered`.
3. `CustomerRegisteredKafkaPublisher` in `adapters/inbound-rest` sends `CustomerRegisteredKafkaMessage` to `customer-registered-events`.
4. `CustomerRegistrationKafkaListener` in `adapters/inbound-kafka` consumes the event.
5. The listener uses the generated OpenFeign client against `http://localhost:8080`.

This verifies that inbound REST and inbound Kafka run as separate local Spring Boot services.

**Kubernetes Separation**

Do not use this `compose.yaml` in k3s. Kubernetes deployments use the `kubernetes` profile, Config Server, and Helm values under `helm/${artifactId}/values.yaml`.

The Docker Compose setup is local development infrastructure only.
