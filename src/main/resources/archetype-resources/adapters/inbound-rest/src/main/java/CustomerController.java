package ${package}.adapters.inbound.rest.controller;

import ${package}.adapters.inbound.rest.converter.CustomerRestConverter;
import ${package}.adapters.inbound.rest.dto.GatewayUserResponse;
import ${package}.adapters.inbound.rest.dto.RegisterCustomerRequest;
import ${package}.adapters.inbound.rest.dto.RegisterCustomerResponse;
import ${package}.adapters.inbound.rest.security.GatewayUserMapper;
import ${package}.application.port.in.RegisterCustomerUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Customer registration endpoints")
public class CustomerController {

    private final RegisterCustomerUseCase registerCustomerUseCase;

    public CustomerController(RegisterCustomerUseCase registerCustomerUseCase) {
        this.registerCustomerUseCase = Objects.requireNonNull(registerCustomerUseCase, "registerCustomerUseCase must not be null");
    }

    @PostMapping
    @Operation(
            summary = "Register a customer",
            description = "Registers a customer from an email address and returns the generated customer id.",
            security = @SecurityRequirement(name = "zitadel"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer registered",
                    content = @Content(schema = @Schema(implementation = RegisterCustomerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid registration request", content = @Content),
            @ApiResponse(responseCode = "409", description = "Customer already exists", content = @Content)
    })
    public ResponseEntity<RegisterCustomerResponse> register(
            @Valid
            @RequestBody(
                    required = true,
                    description = "Customer registration request",
                    content = @Content(schema = @Schema(implementation = RegisterCustomerRequest.class)))
            @org.springframework.web.bind.annotation.RequestBody RegisterCustomerRequest request) {
        var id = registerCustomerUseCase.register(CustomerRestConverter.toCommand(request));
        return ResponseEntity.ok(CustomerRestConverter.toResponse(id));
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current gateway user",
            description = "Returns the authenticated actor from the JWT relayed by spring-gateway-base.",
            security = @SecurityRequirement(name = "zitadel"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authenticated gateway user",
                    content = @Content(schema = @Schema(implementation = GatewayUserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid access token", content = @Content)
    })
    public ResponseEntity<GatewayUserResponse> me(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(GatewayUserMapper.toResponse(jwt));
    }
}
