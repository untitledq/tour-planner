package at.fhtw.swen.tourplanner.controller.exception;

public class UserFavoriteTourNotFoundException extends RuntimeException {
    public UserFavoriteTourNotFoundException(String message) {
        super(message);
    }
}
