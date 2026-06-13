package at.fhtw.swen.tourplanner.service.exception;

public class TourImageMissingException extends RuntimeException {
    public TourImageMissingException(String message) {
        super(message);
    }
}
