package at.fhtw.swen.tourplanner.persistence.exception;

public class TourLogNotFoundException extends RuntimeException {
    public TourLogNotFoundException(String message) {
        super(message);
    }
}
