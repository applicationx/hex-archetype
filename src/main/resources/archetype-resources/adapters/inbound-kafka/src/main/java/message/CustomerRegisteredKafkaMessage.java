package ${package}.adapters.inbound.kafka.message;

import java.util.Objects;
import java.util.UUID;

public record CustomerRegisteredKafkaMessage(UUID customerId) {

    public CustomerRegisteredKafkaMessage {
        Objects.requireNonNull(customerId, "customerId must not be null");
    }
}
