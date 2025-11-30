package com.ecommerce.app.exceptions;


public class APIException extends RuntimeException {
    private final int status;
    private final String message;
    private Object extraDetails;

    public APIException(int status, String message, Object extraDetails) {
        super(message);
        this.status = status;
        this.message = message;
        this.extraDetails = extraDetails;
    }

    public APIException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public APIException(int status, Exception exception) {
        super(exception);
        this.status = status;
        this.message = exception.getLocalizedMessage();
    }

    public APIException(ErrorCodes error, Object... args) {
        this(error.getStatus(), placeArgsInMessage(error.getMessage(), args));
    }

    public APIException() {
        this(ErrorCodes.INTERNAL_SERVER_ERROR);
    }

    private static String placeArgsInMessage(String errorMessage, Object... args) {
        String errorMessageFormat = errorMessage.replaceAll("\\{[0-9]+}", "%s");

        return String.format(errorMessageFormat, args);
    }


    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getExtraDetails() {
        return extraDetails;
    }

}
