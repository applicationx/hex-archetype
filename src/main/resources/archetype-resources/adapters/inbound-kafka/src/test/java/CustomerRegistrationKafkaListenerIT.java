#set($d = '$')
package ${package}.adapters.inbound.kafka.listener;

import ${package}.adapters.inbound.kafka.message.RegisterCustomerKafkaMessage;
import ${package}.application.command.RegisterCustomerCommand;
import ${package}.application.port.in.RegisterCustomerUseCase;
import ${package}.domain.model.CustomerId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                CustomerRegistrationKafkaListenerIT.TestApplication.class,
                CustomerRegistrationKafkaListenerIT.TestUseCaseConfig.class
        },
        properties = {
                "customer.registration.kafka.enabled=true",
                "customer.registration.topic=customer-registration-commands",
                "spring.kafka.consumer.group-id=${rootArtifactId}-inbound-kafka-test",
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
                "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JacksonJsonDeserializer",
                "spring.kafka.consumer.properties.spring.json.trusted.packages=${package}.adapters.inbound.kafka.message",
                "spring.kafka.consumer.properties.spring.json.value.default.type=${package}.adapters.inbound.kafka.message.RegisterCustomerKafkaMessage",
                "spring.kafka.consumer.properties.spring.json.use.type.headers=false",
                "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
                "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JacksonJsonSerializer",
                "spring.kafka.producer.properties.spring.json.add.type.headers=false"
        })
@Testcontainers
class CustomerRegistrationKafkaListenerIT {

    @Container
    static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, RegisterCustomerKafkaMessage> kafkaTemplate;

    @Autowired
    private RecordingRegisterCustomerUseCase registerCustomerUseCase;

    @Test
    void listenerConsumesCustomerRegistrationCommandFromKafka() throws InterruptedException {
        kafkaTemplate.send("customer-registration-commands", new RegisterCustomerKafkaMessage("user@appx-labs.com"));
        kafkaTemplate.flush();

        var command = registerCustomerUseCase.commands.poll(20, TimeUnit.SECONDS);

        assertThat(command).isEqualTo(new RegisterCustomerCommand("user@appx-labs.com"));
    }

    @SpringBootApplication
    @EnableKafka
    static class TestApplication {
    }

    @TestConfiguration
    static class TestUseCaseConfig {

        @Bean
        RecordingRegisterCustomerUseCase registerCustomerUseCase() {
            return new RecordingRegisterCustomerUseCase();
        }

        @Bean
        ConsumerFactory<String, RegisterCustomerKafkaMessage> consumerFactory(
                @Value("${d}{spring.kafka.bootstrap-servers}") String bootstrapServers
        ) {
            Map<String, Object> properties = new HashMap<>();
            properties.put("bootstrap.servers", bootstrapServers);
            properties.put("group.id", "${rootArtifactId}-inbound-kafka-test");
            properties.put("auto.offset.reset", "earliest");
            var valueDeserializer = new JacksonJsonDeserializer<>(RegisterCustomerKafkaMessage.class);
            valueDeserializer.addTrustedPackages("${package}.adapters.inbound.kafka.message");
            valueDeserializer.setUseTypeHeaders(false);
            return new DefaultKafkaConsumerFactory<>(
                    properties,
                    new org.apache.kafka.common.serialization.StringDeserializer(),
                    valueDeserializer
            );
        }

        @Bean
        ConcurrentKafkaListenerContainerFactory<String, RegisterCustomerKafkaMessage> kafkaListenerContainerFactory(
                ConsumerFactory<String, RegisterCustomerKafkaMessage> consumerFactory
        ) {
            var factory = new ConcurrentKafkaListenerContainerFactory<String, RegisterCustomerKafkaMessage>();
            factory.setConsumerFactory(consumerFactory);
            return factory;
        }

        @Bean
        ProducerFactory<String, RegisterCustomerKafkaMessage> producerFactory(
                @Value("${d}{spring.kafka.bootstrap-servers}") String bootstrapServers
        ) {
            Map<String, Object> properties = new HashMap<>();
            properties.put("bootstrap.servers", bootstrapServers);
            properties.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
            properties.put("value.serializer", JacksonJsonSerializer.class);
            properties.put(JacksonJsonSerializer.ADD_TYPE_INFO_HEADERS, false);
            return new DefaultKafkaProducerFactory<>(properties);
        }

        @Bean
        KafkaTemplate<String, RegisterCustomerKafkaMessage> kafkaTemplate(
                ProducerFactory<String, RegisterCustomerKafkaMessage> producerFactory
        ) {
            return new KafkaTemplate<>(producerFactory);
        }
    }

    static final class RecordingRegisterCustomerUseCase implements RegisterCustomerUseCase {

        private final BlockingQueue<RegisterCustomerCommand> commands = new LinkedBlockingQueue<>();

        @Override
        public CustomerId register(RegisterCustomerCommand command) {
            commands.add(command);
            return new CustomerId(UUID.fromString("018f35f8-3b8f-7a8b-8f7d-4c0d2e9d7c2a"));
        }
    }
}
