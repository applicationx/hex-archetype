package ${package}.adapters.inbound.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(name = "GatewayUserResponse", description = "Authenticated user resolved from the gateway-relayed JWT.")
public record GatewayUserResponse(
        @Schema(description = "JWT subject.", example = "370052981241937939")
        String subject,
        @Schema(description = "Preferred username from the identity provider.", example = "appx@appx-labs.com")
        String username,
        @Schema(description = "Email address from the identity provider.", example = "appx@appx-labs.com")
        String email,
        @Schema(description = "Display name from the identity provider.", example = "AppX User")
        String displayName) {

    public GatewayUserResponse {
        subject = Objects.requireNonNullElse(subject, "");
        username = Objects.requireNonNullElse(username, "");
        email = Objects.requireNonNullElse(email, "");
        displayName = Objects.requireNonNullElse(displayName, "");
    }
}
