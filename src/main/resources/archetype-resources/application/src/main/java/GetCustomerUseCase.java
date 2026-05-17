package ${package}.application.port.in;

import ${package}.domain.model.Customer;
import ${package}.domain.model.CustomerId;

public interface GetCustomerUseCase {
    Customer getCustomer(CustomerId customerId);
}
