package ${package}.application.service;

import ${package}.application.command.RegisterCustomerCommand;
import ${package}.application.port.in.RegisterCustomerUseCase;
import ${package}.application.port.out.CustomerRepository;
import ${package}.domain.model.Customer;
import ${package}.domain.model.CustomerId;
import ${package}.domain.model.EmailAddress;

import java.util.Objects;

public final class CustomerApplicationService implements RegisterCustomerUseCase {

    private final CustomerRepository customerRepository;

    public CustomerApplicationService(CustomerRepository customerRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository, "customerRepository must not be null");
    }

    @Override
    public CustomerId register(RegisterCustomerCommand command) {
        var email = new EmailAddress(command.email());

        customerRepository.findByEmail(email).ifPresent(existing -> {
            throw new IllegalStateException("customer already exists for email");
        });

        Customer customer = Customer.registerNew(email);
        return customerRepository.save(customer).id();
    }
}
