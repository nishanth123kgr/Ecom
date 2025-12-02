package com.ecommerce.app.filters;

import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamResolverFilter extends HttpFilter {

    private static HashMap<String, String> pathVariableKeyMap = new HashMap<>();
    private HttpServletRequest request;

    static {
        pathVariableKeyMap.put("users", "userId");
        pathVariableKeyMap.put("orders", "orderId");
        pathVariableKeyMap.put("addresses", "addressId");
        pathVariableKeyMap.put("products", "productId");
        pathVariableKeyMap.put("productVariants", "productVariantId");
        pathVariableKeyMap.put("categories", "categoryId");
        pathVariableKeyMap.put("requests", "requestId");
        pathVariableKeyMap.put("cart", "cartId");
        pathVariableKeyMap.put("cartItems", "cartItemId");
        pathVariableKeyMap.put("sellers", "sellerId");
        pathVariableKeyMap.put("brands", "brandId");
        pathVariableKeyMap.put("discounts", "discountId");
    }


    private HashMap<String, String> getPathVariablesMap(String requestURI) {
        HashMap<String, String> pathVariables = new HashMap<>();

        String pat = "\\w+/\\w+";
        Pattern pattern = Pattern.compile(pat, Pattern.CASE_INSENSITIVE);
        Matcher matches = pattern.matcher(requestURI);

        while (matches.find()) {
            String[] pathPair = matches.group().split("/");

            String pathVariableName = pathPair[0];
            String pathVariableValue = pathPair[1];
            if (!pathVariableKeyMap.containsKey(pathVariableName) || (!pathVariableValue.equals("orders") && !StringUtils.isNumeric(pathVariableValue))) {
                throw new APIException(400, "Invalid Request Path");
            }

            pathVariables.put(pathVariableKeyMap.get(pathVariableName), pathVariableValue);
        }


        return pathVariables;
    }

    private HashMap<String, String> getParams() {
        HashMap<String, String> map = new HashMap<>();

        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            map.put(entry.getKey(), entry.getValue()[0]);
        }

        return map;
    }


    private Map<String, String> parseFormBody() throws IOException {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.startsWith("application/x-www-form-urlencoded")) {
            return new HashMap<>();
        }

        String charset = request.getCharacterEncoding() == null ? "UTF-8" : request.getCharacterEncoding();
        String body = readBody(request, charset);
        if (body == null || body.isEmpty()) return new HashMap<>();

        Map<String, String> map = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx >= 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), charset);
                String value = URLDecoder.decode(pair.substring(idx + 1), charset);
                // if multiple values for same key you can store list; here we keep first
                map.put(key, value);
            } else {
                String key = URLDecoder.decode(pair, charset);
                map.put(key, "");
            }
        }
        return map;
    }

    private String readBody(HttpServletRequest req, String charset) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), charset))) {
            char[] buf = new char[1024];
            int len;
            while ((len = br.read(buf)) != -1) {
                sb.append(buf, 0, len);
            }
        }
        return sb.toString();
    }


    private HashMap<String, String> getQueryParams() {
        HashMap<String, String> queryMap = new HashMap<>();

        String queryString = request.getQueryString();

        if (StringUtils.isNotEmpty(queryString)) {
            for (String query : queryString.split("&")) {
                String[] queryData = query.split("=");
                queryMap.put(queryData[0], queryData[1]);
            }
        }

        return queryMap;
    }


    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        this.request = req;
        String normalizedURI = Utils.normalizeURI(req.getRequestURI(), req.getContextPath());
        if (!normalizedURI.startsWith("/auth")) {
            req.setAttribute("pathParams", getPathVariablesMap(normalizedURI));
        }

        HashMap<String, String> params = getParams();

        if (params.isEmpty() && !req.getMethod().equals("GET") && !req.getMethod().equals("POST")) {
            params = (HashMap<String, String>) parseFormBody();
        }

        req.setAttribute("params", params);

        req.setAttribute("queryParams", getQueryParams());


        chain.doFilter(req, res);
    }
}
