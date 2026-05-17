package ${package}.application.port.out;

public interface DomainEventPublisher {
    void publish(Object event);
}
