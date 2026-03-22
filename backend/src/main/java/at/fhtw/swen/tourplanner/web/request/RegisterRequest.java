package at.fhtw.swen.tourplanner.web.request;

public record RegisterRequest(
        String username,
        String password,
        String email
) {
}