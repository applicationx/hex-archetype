package ${package}.adapters.inbound.rest.error;

import ${package}.application.exception.CustomerAlreadyExistsException;
import ${package}.application.exception.CustomerNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RestExceptionHandlerTest {

    private final RestExceptionHandler handler = new RestExceptionHandler();

    @Test
    void mapsDuplicateCustomerToConflictProblemDetail() {
        var problem = handler.handleCustomerAlreadyExists(new CustomerAlreadyExistsException("user@appx-labs.com"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problem.getTitle()).isEqualTo("Customer already exists");
        assertThat(problem.getDetail()).isEqualTo("Customer already exists for email.");
        assertThat(problem.getType().toString()).isEqualTo("https://errors.appx.local/customer-already-exists");
        assertThat(problem.getProperties()).containsEntry("email", "user@appx-labs.com");
    }

    @Test
    void mapsMissingCustomerToNotFoundProblemDetail() {
        var customerId = UUID.fromString("018f35f8-3b8f-7a8b-8f7d-4c0d2e9d7c2a");

        var problem = handler.handleCustomerNotFound(new CustomerNotFoundException(customerId));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getTitle()).isEqualTo("Customer not found");
        assertThat(problem.getDetail()).isEqualTo("Customer was not found.");
        assertThat(problem.getType().toString()).isEqualTo("https://errors.appx.local/customer-not-found");
        assertThat(problem.getProperties()).containsEntry("customerId", customerId);
    }

    @Test
    void mapsIllegalArgumentToBadRequestProblemDetail() {
        var problem = handler.handleIllegalArgument(new IllegalArgumentException("email must contain '@'"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getTitle()).isEqualTo("Invalid request");
        assertThat(problem.getDetail()).isEqualTo("email must contain '@'");
        assertThat(problem.getType().toString()).isEqualTo("https://errors.appx.local/invalid-request");
    }
}
