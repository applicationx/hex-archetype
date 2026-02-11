package ${package}.domain.model;

import java.util.Objects;

public record EmailAddress(String value) {
    public EmailAddress {
        Objects.requireNonNull(value, "email must not be null");
        if (!value.contains("@")) {
            throw new IllegalArgumentException("email must contain '@'");
        }
    }
}
