package at.fhtw.swen.tourplanner.unit_tests;

import at.fhtw.swen.tourplanner.persistence.entity.User;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.UserRepositoryAdapter;
import at.fhtw.swen.tourplanner.security.core.PasswordHasher;
import at.fhtw.swen.tourplanner.service.dto.UserDto;
import at.fhtw.swen.tourplanner.service.exception.UserDeletionException;
import at.fhtw.swen.tourplanner.service.exception.UserRegistrationException;
import at.fhtw.swen.tourplanner.service.exception.UserValidationException;
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
    private UserRepositoryAdapter userRepositoryAdapter;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private UserServiceImpl userService;

    // REGISTER SUCCESS
    @Test
    void register_shouldCreateUser_whenInputIsValid() {

        when(userRepositoryAdapter.existsByUsername("john")).thenReturn(false);
        when(passwordHasher.hash("password")).thenReturn("hashedPassword");

        when(userRepositoryAdapter.save(any(User.class))).thenAnswer(invocation -> {
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

        verify(userRepositoryAdapter).existsByUsername("john");
        verify(passwordHasher).hash("password");
        verify(userRepositoryAdapter).save(any(User.class));
    }

    // USERNAME ALREADY EXISTS
    @Test
    void register_shouldThrow_whenUsernameAlreadyExists() {

        when(userRepositoryAdapter.existsByUsername("john")).thenReturn(true);

        UserRegistrationException ex = assertThrows(
                UserRegistrationException.class,
                () -> userService.register("john", "password", "mail@test.com")
        );

        assertEquals("Username already exists", ex.getMessage());

        verify(userRepositoryAdapter, never()).save(any());
        verify(passwordHasher, never()).hash(any());
    }

    // INVALID INPUT (BLANK)
    @Test
    void register_shouldThrow_whenCredentialsAreInvalid() {

        assertThrows(UserValidationException.class,
                () -> userService.register("", "password", "mail"));

        assertThrows(UserValidationException.class,
                () -> userService.register("john", "", "mail"));
    }

    // DELETE USER SUCCESS
    @Test
    void deleteUser_shouldDelete_whenUserExists() {

        UUID userId = UUID.randomUUID();

        when(userRepositoryAdapter.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepositoryAdapter).deleteById(userId);
    }

    // DELETE USER NOT FOUND
    @Test
    void deleteUser_shouldThrow_whenUserDoesNotExist() {

        UUID userId = UUID.randomUUID();

        when(userRepositoryAdapter.existsById(userId)).thenReturn(false);

        UserDeletionException ex = assertThrows(
                UserDeletionException.class,
                () -> userService.deleteUser(userId)
        );

        assertEquals("User not found", ex.getMessage());

        verify(userRepositoryAdapter, never()).deleteById(any());
    }

    // VERIFY PASSWORD IS HASHED
    @Test
    void register_shouldStoreHashedPassword() {

        when(userRepositoryAdapter.existsByUsername("john")).thenReturn(false);
        when(passwordHasher.hash("plain")).thenReturn("hashed");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        when(userRepositoryAdapter.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.register("john", "plain", "mail@test.com");

        User savedUser = captor.getValue();

        assertEquals("hashed", savedUser.getPasswordHash());
    }
}