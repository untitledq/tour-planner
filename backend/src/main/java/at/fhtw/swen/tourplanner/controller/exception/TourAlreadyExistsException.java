package at.fhtw.swen.tourplanner.controller.exception;

public class TourAlreadyExistsException extends RuntimeException {
    public TourAlreadyExistsException(String message) {
        super(message);
    }
}
