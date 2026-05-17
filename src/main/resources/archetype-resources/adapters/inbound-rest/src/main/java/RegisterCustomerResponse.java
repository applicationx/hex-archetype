package ${package}.adapters.inbound.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import java.util.UUID;

@Schema(name = "RegisterCustomerResponse", description = "Response body returned after registering a customer.")
public record RegisterCustomerResponse(
        @Schema(
                description = "Generated customer id.",
                example = "018f35f8-3b8f-7a8b-8f7d-4c0d2e9d7c2a",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "uuid")
        UUID customerId) {

    public RegisterCustomerResponse {
        Objects.requireNonNull(customerId, "customerId must not be null");
    }
}
