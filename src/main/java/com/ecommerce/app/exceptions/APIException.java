package com.ecommerce.app.exceptions;

import org.json.JSONObject;

import java.util.HashMap;

public class APIException extends RuntimeException {
    private final int statusCode;
    private final String message;
    private JSONObject extraDetails;

    public APIException(int statusCode, String message, JSONObject extraDetails) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
        this.extraDetails = extraDetails;
    }

    public APIException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }

    public APIException(ErrorCodes error, Object... args) {
        this(error.getStatusCode(), placeArgsInMessage(error.getMessage(), args));
    }

    private static String placeArgsInMessage(String errorMessage, Object... args) {
        String errorMessageFormat = errorMessage.replaceAll("\\{[0-9]+}", "%S");

        return String.format(errorMessageFormat, args);
    }


    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public JSONObject getExtraDetails() {
        return extraDetails;
    }

}
