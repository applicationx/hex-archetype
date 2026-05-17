# Repository Guidance

This repository is a Maven archetype for generating local Spring Boot projects that follow a hexagonal, multi-module layout. Treat `src/main/resources/archetype-resources/` as the generated project template; edits there affect every project created from the archetype.

## Project Shape

- Root `pom.xml`: builds and installs the archetype artifact `com.appx:hexagonal-spring-boot-archetype`.
- `src/main/resources/META-INF/maven/archetype-metadata.xml`: declares required archetype properties and which template files are filtered or packaged.
- `src/main/resources/archetype-resources/`: generated project contents.
- Generated modules:
  - `domain`: domain model and rules.
  - `application`: use cases and ports.
  - `adapters/inbound-rest`: REST adapter.
  - `adapters/inbound-kafka`: Kafka command adapter.
  - `adapters/outbound-jpa`: JPA adapter.
  - `webapp`: Spring Boot composition root with a React/Vite frontend under `src/main/frontend`.
  - `client`: reusable HTTP client artifact.

## Toolchain

- Generated projects require Java 25 and Maven 3.9.15+ through the generated enforcer rules.
- This machine currently has Maven 3.9.15 through `mise`; generated-project verification should run without skipping enforcer when Java 25 is active.
- Keep archetype defaults aligned between:
  - root `README.md`
  - `src/main/resources/META-INF/maven/archetype-metadata.xml`
  - generated parent POM properties in `src/main/resources/archetype-resources/pom.xml`

## Common Commands

Install the archetype locally:

```bash
mvn -B -ntp clean install
```

Generate a local smoke-test project outside the repository:

```bash
rm -rf /tmp/hex-archetype-smoke
mkdir -p /tmp/hex-archetype-smoke
cd /tmp/hex-archetype-smoke
mvn -B -ntp archetype:generate \
  -DarchetypeGroupId=com.appx \
  -DarchetypeArtifactId=hexagonal-spring-boot-archetype \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=com.appx \
  -DartifactId=customer-service \
  -Dversion=0.1.0-SNAPSHOT \
  -Dpackage=com.appx.customers \
  -DjavaVersion=25 \
  -DspringBootVersion=4.0.6 \
  -DspringCloudVersion=2025.1.1 \
  -DinteractiveMode=false
```

Verify a generated project:

```bash
cd /tmp/hex-archetype-smoke/customer-service
mvn -B -ntp verify
```

## Editing Rules

- Preserve package filtering in `archetype-metadata.xml`: Java sources that should land under the requested package must use `packaged="true"`.
- In filtered POM templates, define `#set($d = '$')` and write `${d}{revision}` when the generated file must contain a literal Maven property reference.
- Keep generated module dependencies pointing inward: adapters and `webapp` may depend on `application` and `domain`; `domain` must stay independent of Spring and infrastructure.
- Do not commit generated smoke-test projects or local Maven repository output.
- If adding generated files, update `archetype-metadata.xml` so they are included in archetype output.
- If changing the generated stack or required properties, update both README files and add or adjust smoke-test instructions.
- Frontend source under generated `webapp/src/main/frontend` is intentionally not packaged as Java source; keep it in the webapp module unless creating a deliberate separate frontend module.

## Validation Expectations

For archetype changes, prefer this sequence:

1. `mvn -B -ntp clean install`
2. Generate a project in `/tmp/hex-archetype-smoke`
3. Inspect generated package names and module artifact IDs
4. Run `mvn -B -ntp verify` inside the generated project.
