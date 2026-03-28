package at.fhtw.swen.tourplanner.persistence.port;

public interface PasswordHasher {
    String hash(String rawPassword);
    boolean invalid(String rawPassword, String hashedPassword);
}
