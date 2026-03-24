package at.fhtw.swen.tourplanner.domain.ports;

import at.fhtw.swen.tourplanner.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    boolean existsByUsername(String username);

    User save(User user);

    Optional<User> findByUsername(String username);

    boolean existsById(UUID uuid);

    void deleteById(UUID uuid);
}