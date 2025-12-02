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

    public static HttpServletRequest request = null;

    public static boolean isNotValidEmail(String email) {
        return !EmailValidator.getInstance().isValid(email);
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

    public static void checkAnyFieldExists(Map<String, Object> params, List<String> FIELDS) {
        if (params.isEmpty()) {
            throw new APIException(ErrorCodes.MISSING_PARAM, "Update param");
        }

        for (String field : FIELDS) {
            if (params.containsKey(field)) {
                Object value = params.get(field);
                if (value instanceof String && StringUtils.isEmpty((String) value)) {
                    throw new APIException(ErrorCodes.INVALID_PARAM, field);
                }
                return;
            }
        }
        throw new APIException(ErrorCodes.MISSING_PARAM, "Update Params");
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
        Utils.setResponse(res);


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

    public static Map<String, Object> getParams() {
        return (Map<String, Object>) getRequest().getAttribute("params");
    }

    public static Map<String, Object> getQueryParams() {
        return (Map<String, Object>) getRequest().getAttribute("queryParams");
    }

    public static Map<String, Object> getPathParams() {
        return (Map<String, Object>) getRequest().getAttribute("pathParams");
    }

    public static Map<String, Object> getPayload() {
        return (Map<String, Object>) getRequest().getAttribute("payload");
    }


    public static void safeRollBack(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new APIException();
        }
    }

    public static JedisPool getJedisPool() {
        return (JedisPool) getRequest().getServletContext().getAttribute("JEDIS_POOL");
    }

    public static Cookie getCookie(String tokenName) {
        Cookie[] cookies = getRequest().getCookies();

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

    public static void setRequest(HttpServletRequest request) {
        Map<String, Object> threadLocal = payload.get();
        if (threadLocal != null) {
            threadLocal.put("request", request);
        }
    }

    public static HttpServletRequest getRequest() {
        Map<String, Object> threadLocal = payload.get();
        if (threadLocal != null) {
            return (HttpServletRequest) threadLocal.get("request");
        }
        return null;
    }

    public static Integer getUserID() {
        Map<String, Object> threadLocal = payload.get();
        if (threadLocal != null) {
            return (Integer) threadLocal.getOrDefault("id", 0);
        }
        return 0;
    }

    public static Integer getSellerID() {
        Map<String, Object> threadLocal = payload.get();
        if (threadLocal != null) {
            return (Integer) threadLocal.getOrDefault("seller_id", 0);
        }
        return 0;
    }

    public static String getRole() {
        Map<String, Object> threadLocal = payload.get();
        if (threadLocal != null) {
            return (String) threadLocal.getOrDefault("role", "user");
        }
        return "user";
    }

    public static String getEmail() {
        Map<String, Object> threadLocal = payload.get();
        if (threadLocal != null) {
            return (String) threadLocal.get("email");
        }
        return null;
    }

    public static boolean isAdmin() {
        return getRole().equals("admin");
    }

    public static boolean isSeller() {
        return getRole().equals("seller");
    }

    public static void setPayload(HashMap<String, Object> payloadData) {
        if (payload.get() != null) {
            payload.get().putAll(payloadData);
        } else {
            payload.set(payloadData);
        }

    }

    public static void clearThreadLocal() {
        payload.remove();
    }


    public static Map<String, Object> mapQueryParams(Map<String, Object> query, Map<String, String> map) {
        Map<String, Object> newQuery = new HashMap<>();

        for (Map.Entry<String, Object> entry : query.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.equals("sort_column")) {

                String sortCol = (String) value;
                if (!map.containsKey(sortCol)) {
                    throw new APIException(ErrorCodes.INVALID_PARAM, "Sort Column " + sortCol);
                }

                newQuery.put("sort_column", map.get(sortCol));
                continue;
            }

            if (key.equals("sort_type")) {
                if (((String) value).startsWith("d")) {
                    newQuery.put(key, "desc");
                }
                continue;
            }

            if (key.startsWith("max")) {
                String col = key.split("_", 2)[1];
                if (map.containsKey(col)) {
                    newQuery.put("max_" + map.get(col), value);
                }
            }

            if (key.startsWith("min")) {
                String col = key.split("_", 2)[1];
                if (map.containsKey(col)) {
                    newQuery.put("min_" + map.get(col), value);
                }
                continue;
            }


            newQuery.put(map.getOrDefault(key, key), value);
        }
        return newQuery;
    }


    public static Object cast(String value) {
        String s = value.trim();

        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
        }

        try {
            return Long.parseLong(s);
        } catch (NumberFormatException ignored) {
        }

        try {
            return new java.math.BigInteger(s);
        } catch (NumberFormatException ignored) {
        }

        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ignored) {
        }

        try {
            return new java.math.BigDecimal(s);
        } catch (NumberFormatException ignored) {
        }

        return value;
    }

    public static void setResponse(Response response) {
        getRequest().setAttribute("response", response);
    }

    @SafeVarargs
    public static Map<String, Object> mergeMaps(Map<String, Object>... maps) {
        Map<String, Object> resultMap = new HashMap<>();
        for (Map<String, Object> map : maps) {
            resultMap.putAll(map);
        }

        return resultMap;
    }

    public static void revokeTokens(HttpServletResponse resp, Jedis jedis, String email, Cookie[] cookies) {
        jedis.spop("rftk:" + email);
        for (Cookie cookieObj : cookies) {
            cookieObj.setMaxAge(0);
            resp.addCookie(cookieObj);
        }
    }
}
