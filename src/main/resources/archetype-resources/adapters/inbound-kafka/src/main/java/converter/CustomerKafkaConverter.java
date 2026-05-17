package ${package}.adapters.inbound.kafka.converter;

import ${package}.adapters.inbound.kafka.message.CustomerRegisteredKafkaMessage;
import ${package}.domain.model.CustomerId;

import java.util.Objects;

public final class CustomerKafkaConverter {

    private CustomerKafkaConverter() {
    }

    public static CustomerId toCustomerId(CustomerRegisteredKafkaMessage message) {
        Objects.requireNonNull(message, "message must not be null");
        return new CustomerId(message.customerId());
    }
}
