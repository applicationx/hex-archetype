package ${package}.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailAddressTest {

    @Test
    void rejectsInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> new EmailAddress("not-an-email"));
    }

    @Test
    void acceptsSimpleValidEmail() {
        var email = new EmailAddress("a@b");
        assertEquals("a@b", email.value());
    }
}
