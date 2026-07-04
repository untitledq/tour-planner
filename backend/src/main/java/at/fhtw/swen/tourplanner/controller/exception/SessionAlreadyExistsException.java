package at.fhtw.swen.tourplanner.controller.exception;

public class SessionAlreadyExistsException extends RuntimeException {
    public SessionAlreadyExistsException(String message) {
        super(message);
    }
}
