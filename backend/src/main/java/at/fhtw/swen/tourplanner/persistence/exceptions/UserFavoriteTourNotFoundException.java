package at.fhtw.swen.tourplanner.persistence.exceptions;

public class UserFavoriteTourNotFoundException extends RuntimeException {
    public UserFavoriteTourNotFoundException(String message) {
        super(message);
    }
}
