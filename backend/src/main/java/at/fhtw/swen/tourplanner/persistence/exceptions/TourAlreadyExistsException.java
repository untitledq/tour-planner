package at.fhtw.swen.tourplanner.persistence.exceptions;

public class TourAlreadyExistsException extends RuntimeException {
    public TourAlreadyExistsException(String message) {
        super(message);
    }
}
