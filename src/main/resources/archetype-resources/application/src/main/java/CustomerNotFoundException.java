package ${package}.application.exception;

import java.util.Objects;
import java.util.UUID;

public final class CustomerNotFoundException extends RuntimeException {

    private final UUID customerId;

    public CustomerNotFoundException(UUID customerId) {
        super("Customer not found.");
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
    }

    public UUID customerId() {
        return customerId;
    }
}
