package ${package}.application.command;

import java.util.Objects;

public record RegisterCustomerCommand(String email) {

    public RegisterCustomerCommand {
        Objects.requireNonNull(email, "email must not be null");
    }
}
