package ${package}.adapters.outbound.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class CustomerJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Instant registeredAt;

    protected CustomerJpaEntity() {
    }

    public CustomerJpaEntity(UUID id, String email, Instant registeredAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.registeredAt = Objects.requireNonNull(registeredAt, "registeredAt must not be null");
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }
}
