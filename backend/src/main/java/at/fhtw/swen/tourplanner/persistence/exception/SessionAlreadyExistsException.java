package at.fhtw.swen.tourplanner.persistence.exception;

public class SessionAlreadyExistsException extends RuntimeException {
    public SessionAlreadyExistsException(String message) {
        super(message);
    }
}
