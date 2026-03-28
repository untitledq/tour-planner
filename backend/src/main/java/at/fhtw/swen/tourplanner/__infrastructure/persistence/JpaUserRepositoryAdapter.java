package at.fhtw.swen.tourplanner.__infrastructure.persistence;

import at.fhtw.swen.tourplanner.persistence.repository.SpringDataUserJpaRepository;
import at.fhtw.swen.tourplanner.__domain.model.User;
import at.fhtw.swen.tourplanner.__domain.ports.UserRepository;
import at.fhtw.swen.tourplanner.persistence.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserJpaRepository repository;

    public JpaUserRepositoryAdapter(SpringDataUserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public User save(User user) {
        UserEntity saved = repository.save(toEntity(user));
        return toDomain(saved);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public boolean existsById(UUID uuid) {
        return repository.existsById(uuid);
    }

    @Override
    public void deleteById(UUID uuid) {
        repository.deleteById(uuid);
    }

    private UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.username(),
                user.passwordHash(),
                user.email(),
                user.createdAt()
        );
    }

    private User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getEmail(),
                entity.getCreatedAt()
        );
    }
}