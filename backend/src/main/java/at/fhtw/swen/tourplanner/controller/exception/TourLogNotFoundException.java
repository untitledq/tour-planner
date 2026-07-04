package at.fhtw.swen.tourplanner.controller.exception;

public class TourLogNotFoundException extends RuntimeException {
    public TourLogNotFoundException(String message) {
        super(message);
    }
}
