package ${package}.domain.model;

import java.time.Instant;
import java.util.Objects;

public final class Customer {
    private final CustomerId id;
    private final EmailAddress email;
    private final Instant registeredAt;

    private Customer(CustomerId id, EmailAddress email, Instant registeredAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.registeredAt = Objects.requireNonNull(registeredAt, "registeredAt must not be null");
    }

    public static Customer registerNew(EmailAddress email) {
        return new Customer(CustomerId.newId(), email, Instant.now());
    }

    public static Customer rehydrate(CustomerId id, EmailAddress email, Instant registeredAt) {
        return new Customer(id, email, registeredAt);
    }

    public CustomerId id() {
        return id;
    }

    public EmailAddress email() {
        return email;
    }

    public Instant registeredAt() {
        return registeredAt;
    }
}
