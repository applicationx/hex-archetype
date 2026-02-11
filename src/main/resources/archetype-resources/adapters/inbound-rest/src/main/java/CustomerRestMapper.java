package ${package}.adapters.inbound.rest.mapper;

import ${package}.adapters.inbound.rest.dto.RegisterCustomerRequest;
import ${package}.adapters.inbound.rest.dto.RegisterCustomerResponse;
import ${package}.application.command.RegisterCustomerCommand;
import ${package}.domain.model.CustomerId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerRestMapper {

    RegisterCustomerCommand toCommand(RegisterCustomerRequest request);

    @Mapping(target = "customerId", expression = "java(id.value())")
    RegisterCustomerResponse toResponse(CustomerId id);
}
