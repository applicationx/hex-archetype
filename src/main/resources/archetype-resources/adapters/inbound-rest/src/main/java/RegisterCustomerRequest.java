package ${package}.adapters.inbound.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Schema(name = "RegisterCustomerRequest", description = "Request body for registering a customer.")
public record RegisterCustomerRequest(
        @Schema(
                description = "Customer email address.",
                example = "customer@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "email")
        @NotBlank
        @Email
        String email) {

    public RegisterCustomerRequest {
        Objects.requireNonNull(email, "email must not be null");
    }
}
