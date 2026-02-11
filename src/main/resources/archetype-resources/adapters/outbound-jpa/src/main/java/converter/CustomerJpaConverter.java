package ${package}.adapters.outbound.jpa.converter;

import ${package}.adapters.outbound.jpa.entity.CustomerJpaEntity;
import ${package}.adapters.outbound.jpa.factory.CustomerFactory;
import ${package}.domain.model.Customer;

public final class CustomerJpaConverter {

    private CustomerJpaConverter() {
    }

    public static CustomerJpaEntity toEntity(Customer customer) {
        return new CustomerJpaEntity(customer.id().value(), customer.email().value(), customer.registeredAt());
    }

    public static Customer toDomain(CustomerJpaEntity entity) {
        return CustomerFactory.fromPersistence(entity.getId(), entity.getEmail(), entity.getRegisteredAt());
    }
}
