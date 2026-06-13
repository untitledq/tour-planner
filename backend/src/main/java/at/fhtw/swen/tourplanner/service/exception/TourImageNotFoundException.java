package at.fhtw.swen.tourplanner.service.exception;

public class TourImageNotFoundException extends RuntimeException {
    public TourImageNotFoundException(String message) {
        super(message);
    }
}
