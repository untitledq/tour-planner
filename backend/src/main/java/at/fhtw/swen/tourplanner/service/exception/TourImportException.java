package at.fhtw.swen.tourplanner.service.exception;

public class TourImportException extends RuntimeException {

  public TourImportException(String message) {
    super(message);
  }

  public TourImportException(String message, Throwable cause) {
    super(message, cause);
  }
}