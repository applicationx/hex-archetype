package ${package}.application.exception;

public final class CustomerAlreadyExistsException extends RuntimeException {

    private final String email;

    public CustomerAlreadyExistsException(String email) {
        super("customer already exists for email");
        this.email = email;
    }

    public String email() {
        return email;
    }
}
