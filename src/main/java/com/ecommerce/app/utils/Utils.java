package com.ecommerce.app.utils;

import com.auth0.jwt.interfaces.Claim;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static void checkMandatoryFields(Map<String, Object> params, List<String> MANDATORY_FIELDS) {
        for (String field : MANDATORY_FIELDS) {
            if (!params.containsKey(field)) {
                throw new APIException(ErrorCodes.MISSING_PARAM, field);
            }
            Object value = params.get(field);
            if (value instanceof String && StringUtils.isEmpty((String) value)) {
                throw new APIException(ErrorCodes.INVALID_PARAM, field);
            }
        }
    }

    public static String normalizeURI(String requestURI, String contextPath) {
        String normalizedURI = requestURI.replaceFirst(contextPath, "");
        normalizedURI = normalizedURI.replaceFirst("/api/v[0-9]+", "");
        return normalizedURI;
    }

    public static Map<String, Object> convertClaims(Map<String, Claim> claims) {
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, Claim> entry : claims.entrySet()) {
            String key = entry.getKey();
            if (key.equals("exp") || key.equals("iat")) {
                continue;
            }
            Claim claim = entry.getValue();

            Object value;
            try {
                value = claim.as(Object.class);
            } catch (Exception e) {
                value = null;
            }

            result.put(entry.getKey(), value);
        }

        return result;
    }


}
