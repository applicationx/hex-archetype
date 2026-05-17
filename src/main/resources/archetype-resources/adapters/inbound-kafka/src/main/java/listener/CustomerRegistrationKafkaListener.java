#set($d = '$')
package ${package}.adapters.inbound.kafka.listener;

import ${package}.adapters.inbound.kafka.converter.CustomerKafkaConverter;
import ${package}.adapters.inbound.kafka.message.RegisterCustomerKafkaMessage;
import ${package}.application.port.in.RegisterCustomerUseCase;

import java.util.Objects;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public final class CustomerRegistrationKafkaListener {

    private final RegisterCustomerUseCase registerCustomerUseCase;

    public CustomerRegistrationKafkaListener(RegisterCustomerUseCase registerCustomerUseCase) {
        this.registerCustomerUseCase = Objects.requireNonNull(registerCustomerUseCase, "registerCustomerUseCase must not be null");
    }

    @KafkaListener(
            id = "${rootArtifactId}-customer-registration",
            topics = "${d}{customer.registration.topic:customer-registration-commands}",
            groupId = "${d}{spring.kafka.consumer.group-id:${rootArtifactId}}",
            autoStartup = "${d}{customer.registration.kafka.enabled:false}")
    public void registerCustomer(RegisterCustomerKafkaMessage message) {
        registerCustomerUseCase.register(CustomerKafkaConverter.toCommand(message));
    }
}
