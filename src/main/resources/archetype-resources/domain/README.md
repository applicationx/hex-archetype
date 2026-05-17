# Domain Module

This module contains the business model and rules for `${artifactId}`. It is the innermost layer of the hexagonal architecture.

**Responsibilities**

- Own domain entities, value objects, domain invariants, and pure business behavior.
- Own domain event types that describe meaningful facts that already happened.
- Keep code independent from Spring, persistence, HTTP, messaging, and other infrastructure.
- Provide types that the application layer can use to express use cases.

**Current Contents**

- `Customer`: aggregate-style domain object for a registered customer.
- `CustomerId`: value object wrapping a `UUID`.
- `EmailAddress`: value object with email validation.
- `CustomerRegistered`: domain event emitted after a customer is registered.
- `EmailAddressTest`: focused domain rule tests.

**Dependency Direction**

- This module should not depend on any generated project module.
- Allowed dependencies should be small and domain-oriented. Prefer the Java standard library first.
- Do not import Spring annotations, JPA annotations, REST DTOs, Feign types, or database classes here.

**Where To Make Changes**

- Add or change business rules here when the rule should hold regardless of delivery mechanism or database.
- Add new value objects here when validation or equality semantics matter.
- Add new domain events here when other parts of the system need to react to completed domain facts.
- Add domain tests here for pure rules that do not require Spring or a database.

**Avoid**

- Do not add repositories, controllers, entity annotations, JSON annotations, or framework configuration.
- Do not place request/response DTOs here unless they are true domain concepts.
- Do not make domain objects depend on generated adapter classes.
