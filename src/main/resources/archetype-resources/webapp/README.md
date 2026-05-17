# Webapp Module

This module is the browser frontend for `${artifactId}`. It is a Node/Vite module, not a Spring Boot service.

**Responsibilities**

- Own the React/Vite frontend under `src/main/frontend`.
- Own browser-side routes, components, state, and typed API calls.
- Build frontend assets with Maven through `frontend-maven-plugin` so the full reactor can validate the frontend.

**Current Contents**

- `src/main/frontend`: React/Vite app.
- `package.json`: frontend scripts and dependencies.
- `vite.config.ts`: Vite build configuration.
- `tsconfig.json`: TypeScript configuration.

**Dependency Direction**

- This module must not depend on Java application, adapter, or client modules.
- This module calls backend HTTP APIs through the gateway or through local development URLs.
- Java REST/OpenAPI code belongs in `adapters/inbound-rest`.
- Kafka listener code belongs in `adapters/inbound-kafka`.

**Where To Make Changes**

- Add browser UI changes under `src/main/frontend/src`.
- Add API calls through a small typed frontend client, not directly inside presentation components.
- Add frontend-only tests, linting, and build tooling here.

**Avoid**

- Do not add Spring Boot classes, `application.yml`, controllers, repositories, or integration tests here.
- Do not serve this frontend from the REST service by copying assets into Spring Boot static resources.
- Do not put backend orchestration in browser components. Multi-service browser-facing composition belongs in `spring-gateway-base`.

**Frontend Build**

From the module directory:

```bash
cd webapp/src/main/frontend
npm install
npm run dev
```

From the generated project root:

```bash
mvn -pl webapp -am package
```

For local backend development, run the Java services separately:

```bash
mvn -pl adapters/inbound-rest -am spring-boot:run -Dspring-boot.run.profiles=dev
mvn -pl adapters/inbound-kafka -am spring-boot:run -Dspring-boot.run.profiles=dev
```
