package ${package}.domain.model;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {

    public CustomerId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static CustomerId newId() {
        return new CustomerId(UUID.randomUUID());
    }
}
