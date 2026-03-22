package at.fhtw.swen.tourplanner.domain.ports;

public interface PasswordHasher {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String hashedPassword);
}
