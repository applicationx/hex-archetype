package ${package}.adapters.inbound.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "${package}.adapters.inbound.rest",
        "${package}.adapters.outbound.jpa"
})
@EnableJpaRepositories(basePackages = "${package}.adapters.outbound.jpa.repository")
@EntityScan(basePackages = "${package}.adapters.outbound.jpa.entity")
public class InboundRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(InboundRestApplication.class, args);
    }
}
