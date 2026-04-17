package at.fhtw.swen.tourplanner.security;

public interface PasswordHasher {
    String hash(String rawPassword);
    boolean invalid(String rawPassword, String hashedPassword);
}
