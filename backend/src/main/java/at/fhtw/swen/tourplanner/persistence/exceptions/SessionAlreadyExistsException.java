package at.fhtw.swen.tourplanner.persistence.exceptions;

public class SessionAlreadyExistsException extends RuntimeException {
    public SessionAlreadyExistsException(String message) {
        super(message);
    }
}
