package com.ecommerce.app.exceptions;

public enum ErrorCodes {
    INVALID_ARGUMENT(400, "Bad Request"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    CUSTOM_SERVER_ERROR(500, "{1}"),
    CUSTOM_CLIENT_ERROR(400, "{1}"),
    MISSING_PARAM(400, "{1} is Missing."),
    INVALID_PARAM(400, "{1} is Invalid"),
    NOT_FOUND(404, "{1} not found."),
    UNAUTHORIZED(401, "Invalid User"),
    FORBIDDEN(403, "Insufficient Permissions");



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
