package ${package}.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "${package}")
@EnableJpaRepositories(basePackages = "${package}.adapters.outbound.jpa.repository")
@EntityScan(basePackages = "${package}.adapters.outbound.jpa.entity")
public class MyAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyAppApplication.class, args);
    }
}
