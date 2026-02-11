package ${package}.adapters.outbound.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
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
        this.id = id;
        this.email = email;
        this.registeredAt = registeredAt;
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
