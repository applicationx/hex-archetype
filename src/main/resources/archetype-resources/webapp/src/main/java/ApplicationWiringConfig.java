package ${package}.webapp.config;

import ${package}.application.port.in.GetCustomerUseCase;
import ${package}.application.port.in.RegisterCustomerUseCase;
import ${package}.application.port.out.CustomerRepository;
import ${package}.application.port.out.DomainEventPublisher;
import ${package}.application.service.CustomerApplicationService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationWiringConfig {

    @Bean
    CustomerApplicationService customerApplicationService(CustomerRepository customerRepository, DomainEventPublisher domainEventPublisher) {
        return new CustomerApplicationService(customerRepository, domainEventPublisher);
    }

    @Bean
    RegisterCustomerUseCase registerCustomerUseCase(CustomerApplicationService customerApplicationService) {
        return customerApplicationService;
    }

    @Bean
    GetCustomerUseCase getCustomerUseCase(CustomerApplicationService customerApplicationService) {
        return customerApplicationService;
    }

    @Bean
    DomainEventPublisher domainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return applicationEventPublisher::publishEvent;
    }
}
