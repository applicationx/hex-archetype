package ${package}.adapters.inbound.rest.controller;

import ${package}.adapters.inbound.rest.dto.RegisterCustomerRequest;
import ${package}.adapters.inbound.rest.dto.RegisterCustomerResponse;
import ${package}.adapters.inbound.rest.mapper.CustomerRestMapper;
import ${package}.application.port.in.RegisterCustomerUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final RegisterCustomerUseCase registerCustomerUseCase;
    private final CustomerRestMapper mapper;

    public CustomerController(RegisterCustomerUseCase registerCustomerUseCase, CustomerRestMapper mapper) {
        this.registerCustomerUseCase = registerCustomerUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<RegisterCustomerResponse> register(@RequestBody RegisterCustomerRequest request) {
        var id = registerCustomerUseCase.register(mapper.toCommand(request));
        return ResponseEntity.ok(mapper.toResponse(id));
    }
}
