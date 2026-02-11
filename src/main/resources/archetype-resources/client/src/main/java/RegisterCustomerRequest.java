package ${package}.client.api;

import java.util.Objects;

public record RegisterCustomerRequest(String email) {

    public RegisterCustomerRequest {
        Objects.requireNonNull(email, "email must not be null");
    }
}
