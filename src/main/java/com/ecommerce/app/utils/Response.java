package com.ecommerce.app.utils;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Response(int status, String message, String errorCode, Object data) {
    public Response(int status, String message) {
        this(status, message, (String) null);
    }

    public Response(int status, String message, String errorCode) {
        this(status, message, errorCode, null);
    }

    public Response(int status, Object data) {
        this(status, null, null, data);
    }

    public Response(int status, String message, Object extraDetails) {
        this(status, message, null, extraDetails);
    }
}
