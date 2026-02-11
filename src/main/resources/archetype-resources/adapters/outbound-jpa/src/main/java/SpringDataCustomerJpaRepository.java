package ${package}.adapters.outbound.jpa.repository;

import ${package}.adapters.outbound.jpa.entity.CustomerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataCustomerJpaRepository extends JpaRepository<CustomerJpaEntity, UUID> {
    Optional<CustomerJpaEntity> findByEmail(String email);
}
