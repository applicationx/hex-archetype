package ${package}.adapters.inbound.rest.kafka;

import java.util.Objects;
import java.util.UUID;

public record CustomerRegisteredKafkaMessage(UUID customerId) {

    public CustomerRegisteredKafkaMessage {
        Objects.requireNonNull(customerId, "customerId must not be null");
    }
}
