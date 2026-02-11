package ${package}.webapp.config;

import ${package}.application.port.in.RegisterCustomerUseCase;
import ${package}.application.port.out.CustomerRepository;
import ${package}.application.service.CustomerApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationWiringConfig {

    @Bean
    RegisterCustomerUseCase registerCustomerUseCase(CustomerRepository customerRepository) {
        return new CustomerApplicationService(customerRepository);
    }
}
