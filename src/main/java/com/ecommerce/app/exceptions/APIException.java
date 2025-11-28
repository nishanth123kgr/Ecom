package com.ecommerce.app.exceptions;


public class APIException extends RuntimeException {
    private final int statusCode;
    private final String message;
    private Object extraDetails;

    public APIException(int statusCode, String message, Object extraDetails) {
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

    public APIException(int statusCode, Exception exception) {
        super(exception);
        this.statusCode = statusCode;
        this.message = exception.getLocalizedMessage();
    }

    public APIException(ErrorCodes error, Object... args) {
        this(error.getStatusCode(), placeArgsInMessage(error.getMessage(), args));
    }

    private static String placeArgsInMessage(String errorMessage, Object... args) {
        String errorMessageFormat = errorMessage.replaceAll("\\{[0-9]+}", "%s");

        return String.format(errorMessageFormat, args);
    }


    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Object getExtraDetails() {
        return extraDetails;
    }

}
