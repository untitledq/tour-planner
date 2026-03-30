package at.fhtw.swen.tourplanner.persistence.port;

import at.fhtw.swen.tourplanner.service.dto.UserDto;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    boolean existsByUsername(String username);

    UserDto save(UserDto userDto);

    Optional<UserDto> findByUsername(String username);

    boolean existsById(UUID uuid);

    void deleteById(UUID uuid);
}