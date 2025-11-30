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
    FORBIDDEN(403, "Insufficient Permissions"),
    METHOD_NOT_ALLOWED(405, "{1} method not allowed");



    private final int status;
    private final String message;

    ErrorCodes(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }


}
