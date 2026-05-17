package ${package}.adapters.inbound.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Schema(name = "CustomerResponse", description = "Customer payload returned by the customer API.")
public record CustomerResponse(
        @Schema(
                description = "Customer id.",
                example = "018f35f8-3b8f-7a8b-8f7d-4c0d2e9d7c2a",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "uuid")
        UUID customerId,
        @Schema(
                description = "Registered customer email address.",
                example = "user@appx-labs.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(
                description = "UTC timestamp when the customer was registered.",
                example = "2026-05-17T21:30:00Z",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Instant registeredAt) {

    public CustomerResponse {
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(registeredAt, "registeredAt must not be null");
    }
}
