package ${package}.adapters.outbound.jpa.mapper;

import ${package}.adapters.outbound.jpa.entity.CustomerJpaEntity;
import ${package}.domain.model.Customer;
import ${package}.domain.model.CustomerId;
import ${package}.domain.model.EmailAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerJpaMapper {

    @Mapping(target = "id", expression = "java(customer.id().value())")
    @Mapping(target = "email", expression = "java(customer.email().value())")
    @Mapping(target = "registeredAt", source = "registeredAt")
    CustomerJpaEntity toEntity(Customer customer);

    default Customer toDomain(CustomerJpaEntity entity) {
        return Customer.rehydrate(
                new CustomerId(entity.getId()),
                new EmailAddress(entity.getEmail()),
                entity.getRegisteredAt()
        );
    }
}
