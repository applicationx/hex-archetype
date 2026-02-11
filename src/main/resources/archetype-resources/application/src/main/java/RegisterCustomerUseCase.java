package ${package}.application.port.in;

import ${package}.application.command.RegisterCustomerCommand;
import ${package}.domain.model.CustomerId;

public interface RegisterCustomerUseCase {
    CustomerId register(RegisterCustomerCommand command);
}
