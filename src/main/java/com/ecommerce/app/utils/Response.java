package com.ecommerce.app.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;

import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Response(int statusCode, String message, String errorCode, JSONObject data) {
    public Response(int statusCode, String message) {
        this(statusCode, message, (String) null);
    }

    public Response(int statusCode, String message, String errorCode) {
        this(statusCode, message, errorCode, null);
    }

    public Response(int statusCode, JSONObject data) {
        this(statusCode, null, null, data);
    }

    public Response(int statusCode, String message, JSONObject extraDetails) {
        this(statusCode, message, null, extraDetails);
    }
}
