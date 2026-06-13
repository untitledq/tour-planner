package at.fhtw.swen.tourplanner.persistence.exception;

public class UserFavoriteTourNotFoundException extends RuntimeException {
    public UserFavoriteTourNotFoundException(String message) {
        super(message);
    }
}
