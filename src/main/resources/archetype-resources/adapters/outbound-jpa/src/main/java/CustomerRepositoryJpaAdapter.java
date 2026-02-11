package ${package}.adapters.outbound.jpa.adapter;

import ${package}.adapters.outbound.jpa.mapper.CustomerJpaMapper;
import ${package}.adapters.outbound.jpa.repository.SpringDataCustomerJpaRepository;
import ${package}.application.port.out.CustomerRepository;
import ${package}.domain.model.Customer;
import ${package}.domain.model.EmailAddress;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CustomerRepositoryJpaAdapter implements CustomerRepository {

    private final SpringDataCustomerJpaRepository jpaRepository;
    private final CustomerJpaMapper mapper;

    public CustomerRepositoryJpaAdapter(SpringDataCustomerJpaRepository jpaRepository, CustomerJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Customer> findByEmail(EmailAddress email) {
        return jpaRepository.findByEmail(email.value()).map(mapper::toDomain);
    }

    @Override
    public Customer save(Customer customer) {
        var persisted = jpaRepository.save(mapper.toEntity(customer));
        return mapper.toDomain(persisted);
    }
}
