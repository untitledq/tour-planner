package at.fhtw.swen.tourplanner.infrastructure.persistence;

import at.fhtw.swen.tourplanner.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserJpaRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByUsername(String username);
    Optional<UserEntity> findByUsername(String username);
}
