package ${package}.adapters.inbound.rest.error;

import ${package}.application.exception.CustomerAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
    void mapsIllegalArgumentToBadRequestProblemDetail() {
        var problem = handler.handleIllegalArgument(new IllegalArgumentException("email must contain '@'"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getTitle()).isEqualTo("Invalid request");
        assertThat(problem.getDetail()).isEqualTo("email must contain '@'");
        assertThat(problem.getType().toString()).isEqualTo("https://errors.appx.local/invalid-request");
    }
}
