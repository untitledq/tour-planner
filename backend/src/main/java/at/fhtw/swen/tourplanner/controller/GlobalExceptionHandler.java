package at.fhtw.swen.tourplanner.controller;

import at.fhtw.swen.tourplanner.persistence.exception.TourAlreadyExistsException;
import at.fhtw.swen.tourplanner.persistence.exception.TourLogNotFoundException;
import at.fhtw.swen.tourplanner.persistence.exception.TourNotFoundException;
import at.fhtw.swen.tourplanner.service.exception.TourExportException;
import at.fhtw.swen.tourplanner.service.exception.TourImportException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TourNotFoundException.class)
    public ResponseEntity<?> handleTourNotFound(TourNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TourAlreadyExistsException.class)
    public ResponseEntity<?> handleTourExists(TourAlreadyExistsException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(TourImportException.class)
    public ResponseEntity<?> handleImport(TourImportException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(TourExportException.class)
    public ResponseEntity<?> handleExport(TourExportException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(TourLogNotFoundException.class)
    public ResponseEntity<?> handleTourLogNotFound(TourLogNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<?> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                Map.of(
                        "timestamp", Instant.now().toString(),
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", message
                )
        );
    }
}