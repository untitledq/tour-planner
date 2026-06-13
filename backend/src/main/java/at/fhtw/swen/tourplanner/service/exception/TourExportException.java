package at.fhtw.swen.tourplanner.service.exception;

public class TourExportException extends RuntimeException {

    public TourExportException(String message) {
        super(message);
    }

    public TourExportException(String message, Throwable cause) {
        super(message, cause);
    }
}