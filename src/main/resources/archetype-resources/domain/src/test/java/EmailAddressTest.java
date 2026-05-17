package ${package}.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailAddressTest {

    @Test
    void rejectsInvalidEmail() {
        assertThatThrownBy(() -> new EmailAddress("not-an-email"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void acceptsSimpleValidEmail() {
        var email = new EmailAddress("a@b");
        assertThat(email.value()).isEqualTo("a@b");
    }
}
