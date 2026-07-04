package at.fhtw.swen.tourplanner.controller.exception;

public class TourImageMissingException extends RuntimeException {
    public TourImageMissingException(String message) {
        super(message);
    }
}
