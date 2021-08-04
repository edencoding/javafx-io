package com.edencoding.models.dogs;

public class DogResponse {

    private final String message;
    private final String status;

    public DogResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
