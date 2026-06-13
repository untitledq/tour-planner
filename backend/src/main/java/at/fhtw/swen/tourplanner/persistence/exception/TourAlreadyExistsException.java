package at.fhtw.swen.tourplanner.persistence.exception;

public class TourAlreadyExistsException extends RuntimeException {
    public TourAlreadyExistsException(String message) {
        super(message);
    }
}
