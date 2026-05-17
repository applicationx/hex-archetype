package ${package}.adapters.inbound.kafka;

import ${package}.client.http.MyAppClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "${package}.adapters.inbound.kafka")
@EnableFeignClients(basePackageClasses = MyAppClient.class)
public class InboundKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(InboundKafkaApplication.class, args);
    }
}
