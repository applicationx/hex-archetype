#set($d = '$')
package ${package}.adapters.inbound.kafka.listener;

import ${package}.adapters.inbound.kafka.converter.CustomerKafkaConverter;
import ${package}.adapters.inbound.kafka.message.CustomerRegisteredKafkaMessage;
import ${package}.client.http.MyAppClient;

import java.util.Objects;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public final class CustomerRegistrationKafkaListener {

    private final MyAppClient myAppClient;

    public CustomerRegistrationKafkaListener(MyAppClient myAppClient) {
        this.myAppClient = Objects.requireNonNull(myAppClient, "myAppClient must not be null");
    }

    @KafkaListener(
            id = "${rootArtifactId}-customer-registration",
            topics = "${d}{customer.registration.topic:customer-registered-events}",
            groupId = "${d}{spring.kafka.consumer.group-id:${rootArtifactId}}",
            autoStartup = "${d}{customer.registration.kafka.enabled:false}")
    public void handleCustomerRegistered(CustomerRegisteredKafkaMessage message) {
        var customerId = CustomerKafkaConverter.toCustomerId(message);
        myAppClient.getCustomer(customerId.value());
    }
}
