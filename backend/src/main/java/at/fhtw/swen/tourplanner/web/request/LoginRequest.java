package at.fhtw.swen.tourplanner.web.request;

public record LoginRequest(
        String username,
        String password
) {
}