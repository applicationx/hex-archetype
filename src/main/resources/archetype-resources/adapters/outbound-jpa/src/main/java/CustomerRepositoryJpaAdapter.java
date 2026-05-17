package ${package}.adapters.outbound.jpa.adapter;

import ${package}.adapters.outbound.jpa.converter.CustomerJpaConverter;
import ${package}.adapters.outbound.jpa.repository.SpringDataCustomerJpaRepository;
import ${package}.application.port.out.CustomerRepository;
import ${package}.domain.model.Customer;
import ${package}.domain.model.CustomerId;
import ${package}.domain.model.EmailAddress;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public class CustomerRepositoryJpaAdapter implements CustomerRepository {

    private final SpringDataCustomerJpaRepository jpaRepository;

    public CustomerRepositoryJpaAdapter(SpringDataCustomerJpaRepository jpaRepository) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "jpaRepository must not be null");
    }

    @Override
    public Optional<Customer> findById(CustomerId customerId) {
        return jpaRepository.findById(customerId.value()).map(CustomerJpaConverter::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(EmailAddress email) {
        return jpaRepository.findByEmail(email.value()).map(CustomerJpaConverter::toDomain);
    }

    @Override
    public Customer save(Customer customer) {
        var persisted = jpaRepository.save(CustomerJpaConverter.toEntity(customer));
        return CustomerJpaConverter.toDomain(persisted);
    }
}
