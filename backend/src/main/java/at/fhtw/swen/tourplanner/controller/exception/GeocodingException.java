package at.fhtw.swen.tourplanner.controller.exception;

public class GeocodingException extends RuntimeException {
    public GeocodingException(String message) {
        super(message);
    }
}