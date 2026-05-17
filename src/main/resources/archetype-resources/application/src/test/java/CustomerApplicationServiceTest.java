package ${package}.application.service;

import ${package}.application.command.RegisterCustomerCommand;
import ${package}.application.port.out.CustomerRepository;
import ${package}.application.port.out.DomainEventPublisher;
import ${package}.domain.event.CustomerRegistered;
import ${package}.domain.model.Customer;
import ${package}.domain.model.EmailAddress;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerApplicationServiceTest {

    @Test
    void registersCustomerWhenEmailIsNew() {
        var fakeRepo = new InMemoryCustomerRepository();
        var eventPublisher = new RecordingDomainEventPublisher();
        var service = new CustomerApplicationService(fakeRepo, eventPublisher);

        var id = service.register(new RegisterCustomerCommand("x@y"));

        assertThat(id).isNotNull();
        assertThat(fakeRepo.data).containsKey("x@y");
        assertThat(eventPublisher.events).hasSize(1);

        assertThat(eventPublisher.events.getFirst())
                .isInstanceOfSatisfying(CustomerRegistered.class, event -> {
                    assertThat(event.customerId()).isEqualTo(id);
                    assertThat(event.email().value()).isEqualTo("x@y");
                });
    }

    static final class InMemoryCustomerRepository implements CustomerRepository {
        private final Map<String, Customer> data = new HashMap<>();

        @Override
        public Optional<Customer> findByEmail(EmailAddress email) {
            return Optional.ofNullable(data.get(email.value()));
        }

        @Override
        public Customer save(Customer customer) {
            data.put(customer.email().value(), customer);
            return customer;
        }
    }

    static final class RecordingDomainEventPublisher implements DomainEventPublisher {
        private final List<Object> events = new ArrayList<>();

        @Override
        public void publish(Object event) {
            events.add(event);
        }
    }
}
