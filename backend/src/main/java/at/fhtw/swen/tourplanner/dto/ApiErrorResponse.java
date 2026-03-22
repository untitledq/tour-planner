package at.fhtw.swen.tourplanner.dto;

public class ApiErrorResponse {
    public String error;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(String error) {
        this.error = error;
    }
}
