package com.ecommerce.app.exceptions;

public enum ErrorCodes {
    INVALID_ARGUMENT(400, "Bad Request");

    private final int statusCode;
    private final String message;

    ErrorCodes(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }


}
