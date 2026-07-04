package at.fhtw.swen.tourplanner.controller.exception;

public class UserDeletionException extends RuntimeException {
    public UserDeletionException(String message) {
        super(message);
    }
}
