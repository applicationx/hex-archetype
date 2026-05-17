# Outbound JPA Adapter Module

This module implements persistence using Spring Data JPA. It is an outbound adapter: application services call application ports, and this module fulfills those ports with database access.

**Responsibilities**

- Implement application outbound ports using JPA.
- Own JPA entities, Spring Data repositories, persistence converters, and persistence factories.
- Translate between domain objects and persistence records.
- Keep database-specific concerns out of the domain and application modules.

**Current Contents**

- `CustomerRepositoryJpaAdapter`: implements the application `CustomerRepository` port, including lookup by id and email.
- `SpringDataCustomerJpaRepository`: Spring Data JPA repository.
- `CustomerJpaEntity`: JPA entity mapped to the `customers` table.
- `CustomerJpaConverter`: converts domain objects to JPA entities.
- `CustomerFactory`: reconstructs domain objects from persistence state.

**Dependency Direction**

- This module may depend on `application`.
- It may use domain types through application ports.
- This module must not depend on `webapp`, `client`, or inbound REST adapters.

**Where To Make Changes**

- Add JPA entities here for database tables.
- Add Spring Data repositories here for database access.
- Add adapter implementations here for new outbound persistence ports.
- Add converter/factory code here when translating between persistence and domain models.
- Keep repository methods aligned with application ports, for example `findById`, `findByEmail`, and `save`.

**Avoid**

- Do not expose JPA entities to application services, controllers, or clients.
- Do not add use-case orchestration here; keep it in `application`.
- Do not put HTTP, Feign, or web configuration in this module.
