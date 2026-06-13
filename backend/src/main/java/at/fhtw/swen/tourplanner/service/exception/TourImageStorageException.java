package at.fhtw.swen.tourplanner.service.exception;

import java.io.IOException;

public class TourImageStorageException extends RuntimeException {
    public TourImageStorageException(String message) {
        super(message);
    }
}
