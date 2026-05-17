# Application Module

This module contains use cases and ports for `${artifactId}`. It coordinates domain behavior without knowing how requests arrive or where data is stored.

**Responsibilities**

- Define inbound use-case interfaces.
- Define outbound ports required by use cases.
- Implement application services that orchestrate domain objects.
- Hold command/query objects that represent use-case inputs.
- Publish domain events through outbound ports without depending on Spring or messaging infrastructure.

**Current Contents**

- `RegisterCustomerUseCase`: inbound port for customer registration.
- `GetCustomerUseCase`: inbound port for retrieving a registered customer payload.
- `RegisterCustomerCommand`: input command for registration.
- `CustomerRepository`: outbound persistence port.
- `DomainEventPublisher`: outbound event publication port.
- `CustomerAlreadyExistsException`: typed application exception translated by inbound adapters.
- `CustomerNotFoundException`: typed application exception translated by inbound adapters.
- `CustomerApplicationService`: use-case implementation.
- `CustomerApplicationServiceTest`: unit test using an in-memory fake repository.

**Current Flow**

- `register(RegisterCustomerCommand)` validates uniqueness, stores the customer, publishes `CustomerRegistered`, and returns the new `CustomerId`.
- `getCustomer(CustomerId)` loads the registered customer through `CustomerRepository.findById` and throws `CustomerNotFoundException` when missing.
- Domain events are published through `DomainEventPublisher`; Spring and Kafka integration happens outside this module.

**Dependency Direction**

- This module may depend on `domain`.
- This module must not depend on `webapp`, `client`, `adapters/inbound-rest`, or `adapters/outbound-jpa`.
- Application services should depend on outbound port interfaces, not adapter implementations.

**Where To Make Changes**

- Add a new use case here when behavior is part of the application workflow.
- Add new outbound ports here when a use case needs persistence, external APIs, messaging, or other infrastructure.
- Add command/query types here when they model use-case inputs rather than transport-specific JSON.
- Publish domain events from application services after durable state changes complete.
- Add unit tests here for orchestration logic and port interactions.

**Avoid**

- Do not add Spring MVC controllers, JPA repositories, Feign clients, or database entities.
- Do not expose adapter DTOs from use-case interfaces.
- Do not put infrastructure error handling here unless it is translated into application-level behavior.
