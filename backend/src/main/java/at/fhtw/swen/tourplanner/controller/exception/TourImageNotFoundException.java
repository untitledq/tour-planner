package at.fhtw.swen.tourplanner.controller.exception;

public class TourImageNotFoundException extends RuntimeException {
    public TourImageNotFoundException(String message) {
        super(message);
    }
}
