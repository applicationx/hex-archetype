package ${package}.client.test;

import ${package}.client.api.CustomerResponse;

import java.time.Instant;
import java.util.UUID;

public final class CustomerClientTestFixtures {

    private CustomerClientTestFixtures() {
    }

    public static CustomerResponse customerResponse() {
        return new CustomerResponse(
                UUID.fromString("018f35f8-3b8f-7a8b-8f7d-4c0d2e9d7c2a"),
                "user@appx-labs.com",
                Instant.parse("2026-05-17T21:30:00Z")
        );
    }
}
