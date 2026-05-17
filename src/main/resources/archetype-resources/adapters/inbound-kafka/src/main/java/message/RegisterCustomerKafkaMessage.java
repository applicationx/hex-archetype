package ${package}.adapters.inbound.kafka.message;

import java.util.Objects;

public record RegisterCustomerKafkaMessage(String email) {

    public RegisterCustomerKafkaMessage {
        Objects.requireNonNull(email, "email must not be null");
    }
}
