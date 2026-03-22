package at.fhtw.swen.tourplanner.domain.ports;

import at.fhtw.swen.tourplanner.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    boolean existsByUsername(String username);

    User save(User user);

    Optional<User> findByUsername(String username);
}