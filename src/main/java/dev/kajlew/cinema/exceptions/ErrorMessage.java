package dev.kajlew.cinema.exceptions;

public class ErrorMessage {
    private String error;

    public ErrorMessage(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
