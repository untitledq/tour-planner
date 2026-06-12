package at.fhtw.swen.tourplanner.unit_tests;

import at.fhtw.swen.tourplanner.persistence.entity.User;
import at.fhtw.swen.tourplanner.persistence.repository.UserRepository;
import at.fhtw.swen.tourplanner.security.core.PasswordHasher;
import at.fhtw.swen.tourplanner.service.dto.UserDto;
import at.fhtw.swen.tourplanner.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private UserServiceImpl userService;

    // REGISTER SUCCESS
    @Test
    void register_shouldCreateUser_whenInputIsValid() {

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordHasher.hash("password")).thenReturn("hashedPassword");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            return invocation.<User>getArgument(0); // just return persisted entity
        });

        UserDto result = userService.register(
                "john",
                "password",
                "john@test.com"
        );

        assertNotNull(result);
        assertEquals("john", result.username());
        assertEquals("hashedPassword", result.passwordHash());
        assertEquals("john@test.com", result.email());

        verify(userRepository).existsByUsername("john");
        verify(passwordHasher).hash("password");
        verify(userRepository).save(any(User.class));
    }

    // USERNAME ALREADY EXISTS
    @Test
    void register_shouldThrow_whenUsernameAlreadyExists() {

        when(userRepository.existsByUsername("john")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register("john", "password", "mail@test.com")
        );

        assertEquals("username already exists", ex.getMessage());

        verify(userRepository, never()).save(any());
        verify(passwordHasher, never()).hash(any());
    }

    // INVALID INPUT (BLANK)
    @Test
    void register_shouldThrow_whenCredentialsAreInvalid() {

        assertThrows(IllegalArgumentException.class,
                () -> userService.register("", "password", "mail"));

        assertThrows(IllegalArgumentException.class,
                () -> userService.register("john", "", "mail"));
    }

    // DELETE USER SUCCESS
    @Test
    void deleteUser_shouldDelete_whenUserExists() {

        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    // DELETE USER NOT FOUND
    @Test
    void deleteUser_shouldThrow_whenUserDoesNotExist() {

        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteUser(userId)
        );

        assertEquals("User not found", ex.getMessage());

        verify(userRepository, never()).deleteById(any());
    }

    // VERIFY PASSWORD IS HASHED
    @Test
    void register_shouldStoreHashedPassword() {

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordHasher.hash("plain")).thenReturn("hashed");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        when(userRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.register("john", "plain", "mail@test.com");

        User savedUser = captor.getValue();

        assertEquals("hashed", savedUser.getPasswordHash());
    }
}