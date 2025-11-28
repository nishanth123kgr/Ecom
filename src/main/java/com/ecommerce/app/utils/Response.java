package com.ecommerce.app.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Response(int statusCode, String message, String errorCode, Object data) {
    public Response(int statusCode, String message) {
        this(statusCode, message, (String) null);
    }

    public Response(int statusCode, String message, String errorCode) {
        this(statusCode, message, errorCode, null);
    }

    public Response(int statusCode, Object data) {
        this(statusCode, null, null, data);
    }

    public Response(int statusCode, String message, Object extraDetails) {
        this(statusCode, message, null, extraDetails);
    }
}
