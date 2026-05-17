#set($d = '$')
package ${package}.adapters.inbound.kafka.listener;

import ${package}.adapters.inbound.kafka.message.CustomerRegisteredKafkaMessage;
import ${package}.client.http.MyAppClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
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
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

@SpringBootTest(
        classes = {
                CustomerRegistrationKafkaListenerIT.TestApplication.class,
                CustomerRegistrationKafkaListenerIT.TestKafkaConfig.class
        },
        properties = {
                "customer.registration.kafka.enabled=true",
                "customer.registration.topic=customer-registered-events",
                "spring.kafka.consumer.group-id=${rootArtifactId}-inbound-kafka-test",
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
                "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JacksonJsonDeserializer",
                "spring.kafka.consumer.properties.spring.json.trusted.packages=${package}.adapters.inbound.kafka.message",
                "spring.kafka.consumer.properties.spring.json.value.default.type=${package}.adapters.inbound.kafka.message.CustomerRegisteredKafkaMessage",
                "spring.kafka.consumer.properties.spring.json.use.type.headers=false",
                "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
                "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JacksonJsonSerializer",
                "spring.kafka.producer.properties.spring.json.add.type.headers=false"
        })
@Testcontainers
@EnableWireMock(@ConfigureWireMock(baseUrlProperties = "${rootArtifactId}.client.url"))
class CustomerRegistrationKafkaListenerIT {

    private static final UUID CUSTOMER_ID = UUID.fromString("018f35f8-3b8f-7a8b-8f7d-4c0d2e9d7c2a");

    @Container
    static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, CustomerRegisteredKafkaMessage> kafkaTemplate;

    @Test
    void listenerConsumesCustomerRegisteredEventAndFetchesCustomerPayload() throws InterruptedException {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/api/v1/customers/" + CUSTOMER_ID))
                .willReturn(okJson("""
                        {
                          "customerId": "018f35f8-3b8f-7a8b-8f7d-4c0d2e9d7c2a",
                          "email": "user@appx-labs.com",
                          "registeredAt": "2026-05-17T21:30:00Z"
                        }
                        """)));

        kafkaTemplate.send("customer-registered-events", new CustomerRegisteredKafkaMessage(CUSTOMER_ID));
        kafkaTemplate.flush();

        assertThat(awaitCustomerPayloadRequest()).isTrue();
    }

    @SpringBootApplication
    @EnableKafka
    @EnableFeignClients(basePackageClasses = MyAppClient.class)
    static class TestApplication {
    }

    @TestConfiguration
    static class TestKafkaConfig {

        @Bean
        ConsumerFactory<String, CustomerRegisteredKafkaMessage> consumerFactory(
                @Value("${d}{spring.kafka.bootstrap-servers}") String bootstrapServers
        ) {
            Map<String, Object> properties = new HashMap<>();
            properties.put("bootstrap.servers", bootstrapServers);
            properties.put("group.id", "${rootArtifactId}-inbound-kafka-test");
            properties.put("auto.offset.reset", "earliest");
            var valueDeserializer = new JacksonJsonDeserializer<>(CustomerRegisteredKafkaMessage.class);
            valueDeserializer.addTrustedPackages("${package}.adapters.inbound.kafka.message");
            valueDeserializer.setUseTypeHeaders(false);
            return new DefaultKafkaConsumerFactory<>(
                    properties,
                    new org.apache.kafka.common.serialization.StringDeserializer(),
                    valueDeserializer
            );
        }

        @Bean
        ConcurrentKafkaListenerContainerFactory<String, CustomerRegisteredKafkaMessage> kafkaListenerContainerFactory(
                ConsumerFactory<String, CustomerRegisteredKafkaMessage> consumerFactory
        ) {
            var factory = new ConcurrentKafkaListenerContainerFactory<String, CustomerRegisteredKafkaMessage>();
            factory.setConsumerFactory(consumerFactory);
            return factory;
        }

        @Bean
        ProducerFactory<String, CustomerRegisteredKafkaMessage> producerFactory(
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
        KafkaTemplate<String, CustomerRegisteredKafkaMessage> kafkaTemplate(
                ProducerFactory<String, CustomerRegisteredKafkaMessage> producerFactory
        ) {
            return new KafkaTemplate<>(producerFactory);
        }
    }

    private boolean awaitCustomerPayloadRequest() throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(20);
        AssertionError lastError = null;
        while (System.nanoTime() < deadline) {
            try {
                verify(getRequestedFor(urlEqualTo("/api/v1/customers/" + CUSTOMER_ID)));
                return true;
            } catch (AssertionError error) {
                lastError = error;
                TimeUnit.MILLISECONDS.sleep(250);
            }
        }
        if (lastError != null) {
            throw lastError;
        }
        return false;
    }
}
