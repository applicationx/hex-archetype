package ${package}.adapters.inbound.rest.controller;

import ${package}.adapters.inbound.rest.dto.RegisterCustomerRequest;
import ${package}.application.command.RegisterCustomerCommand;
import ${package}.application.port.in.RegisterCustomerUseCase;
import ${package}.domain.model.CustomerId;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerControllerTest {

    @Test
    void registerDelegatesToUseCaseAndReturnsCustomerId() {
        var generatedId = UUID.fromString("018f35f8-3b8f-7a8b-8f7d-4c0d2e9d7c2a");
        RegisterCustomerUseCase useCase = mock(RegisterCustomerUseCase.class);
        when(useCase.register(any(RegisterCustomerCommand.class))).thenReturn(new CustomerId(generatedId));
        var controller = new CustomerController(useCase);

        var response = controller.register(new RegisterCustomerRequest("user@appx-labs.com"));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().customerId()).isEqualTo(generatedId);
        verify(useCase).register(new RegisterCustomerCommand("user@appx-labs.com"));
    }

    @Test
    void meMapsGatewayJwtClaimsToResponse() {
        var jwt = new Jwt(
                "token",
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T01:00:00Z"),
                Map.of("alg", "none"),
                Map.of(
                        "sub", "370052981241937939",
                        "preferred_username", "appx@appx-labs.com",
                        "email", "appx@appx-labs.com",
                        "name", "AppX User"
                )
        );
        var controller = new CustomerController(mock(RegisterCustomerUseCase.class));

        var response = controller.me(jwt);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().subject()).isEqualTo("370052981241937939");
        assertThat(response.getBody().username()).isEqualTo("appx@appx-labs.com");
        assertThat(response.getBody().email()).isEqualTo("appx@appx-labs.com");
        assertThat(response.getBody().displayName()).isEqualTo("AppX User");
    }
}
