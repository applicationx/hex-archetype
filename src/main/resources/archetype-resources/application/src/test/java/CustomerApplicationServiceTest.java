package ${package}.application.service;

import ${package}.application.command.RegisterCustomerCommand;
import ${package}.application.port.out.CustomerRepository;
import ${package}.domain.model.Customer;
import ${package}.domain.model.EmailAddress;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerApplicationServiceTest {

    @Test
    void registersCustomerWhenEmailIsNew() {
        var fakeRepo = new InMemoryCustomerRepository();
        var service = new CustomerApplicationService(fakeRepo);

        var id = service.register(new RegisterCustomerCommand("x@y"));

        assertNotNull(id);
        assertTrue(fakeRepo.data.containsKey("x@y"));
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
}
