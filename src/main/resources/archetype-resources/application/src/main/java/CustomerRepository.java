package ${package}.application.port.out;

import ${package}.domain.model.Customer;
import ${package}.domain.model.CustomerId;
import ${package}.domain.model.EmailAddress;

import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findById(CustomerId customerId);

    Optional<Customer> findByEmail(EmailAddress email);

    Customer save(Customer customer);
}
