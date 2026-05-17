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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerApplicationServiceTest {

    @Test
    void registersCustomerWhenEmailIsNew() {
        var fakeRepo = new InMemoryCustomerRepository();
        var eventPublisher = new RecordingDomainEventPublisher();
        var service = new CustomerApplicationService(fakeRepo, eventPublisher);

        var id = service.register(new RegisterCustomerCommand("x@y"));

        assertNotNull(id);
        assertTrue(fakeRepo.data.containsKey("x@y"));
        assertEquals(1, eventPublisher.events.size());

        var event = assertInstanceOf(CustomerRegistered.class, eventPublisher.events.getFirst());
        assertEquals(id, event.customerId());
        assertEquals("x@y", event.email().value());
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
