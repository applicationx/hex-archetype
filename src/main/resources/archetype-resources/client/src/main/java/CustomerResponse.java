package ${package}.client.api;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record CustomerResponse(UUID customerId, String email, Instant registeredAt) {

    public CustomerResponse {
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(registeredAt, "registeredAt must not be null");
    }
}
