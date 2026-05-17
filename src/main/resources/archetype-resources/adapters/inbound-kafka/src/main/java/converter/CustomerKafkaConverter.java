package ${package}.adapters.inbound.kafka.converter;

import ${package}.adapters.inbound.kafka.message.RegisterCustomerKafkaMessage;
import ${package}.application.command.RegisterCustomerCommand;

import java.util.Objects;

public final class CustomerKafkaConverter {

    private CustomerKafkaConverter() {
    }

    public static RegisterCustomerCommand toCommand(RegisterCustomerKafkaMessage message) {
        Objects.requireNonNull(message, "message must not be null");
        return new RegisterCustomerCommand(message.email());
    }
}
