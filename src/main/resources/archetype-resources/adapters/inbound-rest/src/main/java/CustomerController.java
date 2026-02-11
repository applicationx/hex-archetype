package ${package}.adapters.inbound.rest.controller;

import ${package}.adapters.inbound.rest.converter.CustomerRestConverter;
import ${package}.adapters.inbound.rest.dto.RegisterCustomerRequest;
import ${package}.adapters.inbound.rest.dto.RegisterCustomerResponse;
import ${package}.application.port.in.RegisterCustomerUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final RegisterCustomerUseCase registerCustomerUseCase;

    public CustomerController(RegisterCustomerUseCase registerCustomerUseCase) {
        this.registerCustomerUseCase = Objects.requireNonNull(registerCustomerUseCase, "registerCustomerUseCase must not be null");
    }

    @PostMapping
    public ResponseEntity<RegisterCustomerResponse> register(@RequestBody RegisterCustomerRequest request) {
        var id = registerCustomerUseCase.register(CustomerRestConverter.toCommand(request));
        return ResponseEntity.ok(CustomerRestConverter.toResponse(id));
    }
}
