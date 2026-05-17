package ${package}.application.service;

import ${package}.application.command.RegisterCustomerCommand;
import ${package}.application.exception.CustomerAlreadyExistsException;
import ${package}.application.exception.CustomerNotFoundException;
import ${package}.application.port.in.GetCustomerUseCase;
import ${package}.application.port.in.RegisterCustomerUseCase;
import ${package}.application.port.out.CustomerRepository;
import ${package}.application.port.out.DomainEventPublisher;
import ${package}.domain.event.CustomerRegistered;
import ${package}.domain.model.Customer;
import ${package}.domain.model.CustomerId;
import ${package}.domain.model.EmailAddress;

import java.util.Objects;

public final class CustomerApplicationService implements RegisterCustomerUseCase, GetCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final DomainEventPublisher domainEventPublisher;

    public CustomerApplicationService(CustomerRepository customerRepository, DomainEventPublisher domainEventPublisher) {
        this.customerRepository = Objects.requireNonNull(customerRepository, "customerRepository must not be null");
        this.domainEventPublisher = Objects.requireNonNull(domainEventPublisher, "domainEventPublisher must not be null");
    }

    @Override
    public CustomerId register(RegisterCustomerCommand command) {
        var email = new EmailAddress(command.email());

        customerRepository.findByEmail(email).ifPresent(existing -> {
            throw new CustomerAlreadyExistsException(email.value());
        });

        Customer customer = Customer.registerNew(email);
        Customer saved = customerRepository.save(customer);

        domainEventPublisher.publish(new CustomerRegistered(saved.id(), saved.email(), saved.registeredAt()));

        return saved.id();
    }

    @Override
    public Customer getCustomer(CustomerId customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId.value()));
    }
}
