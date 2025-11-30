package com.ecommerce.app.utils;

import com.auth0.jwt.interfaces.Claim;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utils {

    private static final ThreadLocal<HashMap<String, Object>> payload = new ThreadLocal<>();

    public static String ACCESS_TOKEN = "accessToken";
    public static String REFRESH_TOKEN = "refreshToken";

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


    public static void setTokenInRequest(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> tokens, String email) {
        Response res = new Response(200, tokens);

        Cookie accessCookie = new Cookie("accessToken", (String) tokens.get("accessToken"));
        accessCookie.setPath("/");

        Cookie refreshCookie = new Cookie("refreshToken", (String) tokens.get("refreshToken"));
        refreshCookie.setPath("/");

        resp.addCookie(accessCookie);
        resp.addCookie(refreshCookie);
        req.setAttribute("response", res);


        JedisPool pool = (JedisPool) req.getServletContext().getAttribute("JEDIS_POOL");

        String[] split = refreshCookie.getValue().split("\\.");

        String key = split[split.length - 1];


        try (Jedis connection = pool.getResource()) {
            connection.sadd("rftk:" + email, "rftks:" + key);
        }

    }

    public static void refreshTokens(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> payload, String email, Jedis jedis, String key) {
        AuthService service = new AuthService();
        Map<String, Object> tokens = service.getTokens(payload);

        Utils.setTokenInRequest(req, resp, tokens, email);

        jedis.srem("rftk:" + email, "rftks:" + key);
    }

    public static Map<String, Object> getParams(HttpServletRequest request) {
        return (Map<String, Object>) request.getAttribute("params");
    }

    public static Map<String, Object> getQueryParams(HttpServletRequest request) {
        return (Map<String, Object>) request.getAttribute("queryParams");
    }

    public static Map<String, Object> getPathParams(HttpServletRequest request) {
        return (Map<String, Object>) request.getAttribute("pathParams");
    }

    public static Map<String, Object> getPayload(HttpServletRequest request) {
        return (Map<String, Object>) request.getAttribute("payload");
    }


    public static void safeRollBack(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new APIException();
        }
    }

    public static JedisPool getJedisPool(HttpServletRequest request) {
        return (JedisPool) request.getServletContext().getAttribute("JEDIS_POOL");
    }

    public static Cookie getCookie(HttpServletRequest request, String tokenName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new APIException(ErrorCodes.UNAUTHORIZED);
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(tokenName)) {
                return cookie;
            }
        }
        throw new APIException(ErrorCodes.UNAUTHORIZED);
    }

    public static String getTokenKey(String token) {
        String[] split = token.split("\\.");

        return split[split.length - 1];
    }

    public static void validateNotAllowedFieldsForUpdate(Map<String, Object> params, Set<String> fields) {
        for (String key : params.keySet()) {
            if (fields.contains(key.toLowerCase())) {
                throw new APIException(ErrorCodes.FORBIDDEN);
            }
        }
    }

    public static Integer getUserID() {
        Map<String, Object> threadLocal = payload.get();
        if (threadLocal != null) {
            return (Integer) threadLocal.get("id");
        }
        return 0;
    }

    public static String getRole() {
        Map<String, Object> threadLocal = payload.get();
        if (threadLocal != null) {
            return (String) threadLocal.get("role");
        }
        return "user";
    }

    public static boolean isAdmin() {
        return getRole().equals("admin");
    }

    public static boolean isSeller() {
        return getRole().equals("user");
    }

    public static void setPayload(HashMap<String, Object> payloadData) {
        payload.set(payloadData);
    }

    public static void clearThreadLocal() {
        payload.remove();
    }

}
