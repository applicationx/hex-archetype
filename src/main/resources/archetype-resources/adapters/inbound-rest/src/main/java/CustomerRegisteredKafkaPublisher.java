#set($d = '$')
package ${package}.adapters.inbound.rest.kafka;

import ${package}.domain.event.CustomerRegistered;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@ConditionalOnProperty(prefix = "customer.registration.kafka", name = "enabled", havingValue = "true")
public final class CustomerRegisteredKafkaPublisher {

    private final KafkaTemplate<String, CustomerRegisteredKafkaMessage> kafkaTemplate;
    private final String topic;

    public CustomerRegisteredKafkaPublisher(
            KafkaTemplate<String, CustomerRegisteredKafkaMessage> kafkaTemplate,
            @Value("${d}{customer.registration.topic:customer-registered-events}") String topic
    ) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate, "kafkaTemplate must not be null");
        this.topic = Objects.requireNonNull(topic, "topic must not be null");
    }

    @EventListener
    public void publish(CustomerRegistered event) {
        kafkaTemplate.send(topic, event.customerId().value().toString(), new CustomerRegisteredKafkaMessage(event.customerId().value()));
    }
}
