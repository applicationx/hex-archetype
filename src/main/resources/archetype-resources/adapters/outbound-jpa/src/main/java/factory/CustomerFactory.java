package ${package}.adapters.outbound.jpa.factory;

import ${package}.domain.model.Customer;
import ${package}.domain.model.CustomerId;
import ${package}.domain.model.EmailAddress;

import java.time.Instant;
import java.util.UUID;

public final class CustomerFactory {

    private CustomerFactory() {
    }

    public static Customer fromPersistence(UUID id, String email, Instant registeredAt) {
        return Customer.rehydrate(new CustomerId(id), new EmailAddress(email), registeredAt);
    }
}
