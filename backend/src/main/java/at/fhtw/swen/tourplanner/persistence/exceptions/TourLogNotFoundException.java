package at.fhtw.swen.tourplanner.persistence.exceptions;

public class TourLogNotFoundException extends RuntimeException {
    public TourLogNotFoundException(String message) {
        super(message);
    }
}
