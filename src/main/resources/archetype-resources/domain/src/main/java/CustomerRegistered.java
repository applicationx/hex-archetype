package ${package}.domain.event;

import ${package}.domain.model.CustomerId;
import ${package}.domain.model.EmailAddress;

import java.time.Instant;
import java.util.Objects;

public record CustomerRegistered(CustomerId customerId, EmailAddress email, Instant registeredAt) {

    public CustomerRegistered {
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(registeredAt, "registeredAt must not be null");
    }
}
