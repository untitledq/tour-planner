package at.fhtw.swen.tourplanner.persistence.repository;

import at.fhtw.swen.tourplanner.persistence.entity.User;
import at.fhtw.swen.tourplanner.persistence.exception.UserAlreadyExistsException;
import at.fhtw.swen.tourplanner.persistence.exception.UserNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter {

    private final UserRepository userRepository;

    public UserRepositoryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User with id " + id + " not found"));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User with username '" + username + "' not found"));
    }

    public User save(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException(
                    "User with username '" + user.getUsername() + "' already exists");
        }

        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void deleteById(UUID id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}