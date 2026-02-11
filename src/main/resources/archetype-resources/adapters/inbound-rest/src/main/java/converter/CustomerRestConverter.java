package ${package}.adapters.inbound.rest.converter;

import ${package}.adapters.inbound.rest.dto.RegisterCustomerRequest;
import ${package}.adapters.inbound.rest.dto.RegisterCustomerResponse;
import ${package}.application.command.RegisterCustomerCommand;
import ${package}.domain.model.CustomerId;

public final class CustomerRestConverter {

    private CustomerRestConverter() {
    }

    public static RegisterCustomerCommand toCommand(RegisterCustomerRequest request) {
        return new RegisterCustomerCommand(request.email());
    }

    public static RegisterCustomerResponse toResponse(CustomerId id) {
        return new RegisterCustomerResponse(id.value());
    }
}
