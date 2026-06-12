package at.fhtw.swen.tourplanner.service.exception;

public class UserDeletionException extends RuntimeException {
    public UserDeletionException(String message) {
        super(message);
    }
}
