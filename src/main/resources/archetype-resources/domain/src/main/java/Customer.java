package ${package}.domain.model;

import java.time.Instant;
import java.util.Objects;

public record Customer(CustomerId id, EmailAddress email, Instant registeredAt) {

    public Customer {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(registeredAt, "registeredAt must not be null");
    }

    public static Customer registerNew(EmailAddress email) {
        return new Customer(CustomerId.newId(), email, Instant.now());
    }

    public static Customer rehydrate(CustomerId id, EmailAddress email, Instant registeredAt) {
        return new Customer(id, email, registeredAt);
    }
}
