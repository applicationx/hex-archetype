package ${package}.adapters.inbound.rest.dto;

import java.util.Objects;
import java.util.UUID;

public record RegisterCustomerResponse(UUID customerId) {

    public RegisterCustomerResponse {
        Objects.requireNonNull(customerId, "customerId must not be null");
    }
}
