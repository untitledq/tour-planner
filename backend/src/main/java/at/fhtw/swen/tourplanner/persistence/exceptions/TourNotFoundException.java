package at.fhtw.swen.tourplanner.persistence.exceptions;

public class TourNotFoundException extends RuntimeException {
    public TourNotFoundException(String message) {
        super(message);
    }
}
