package com.ecommerce.app.filters;

import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
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
            if (!pathVariableKeyMap.containsKey(pathVariableName) || !StringUtils.isNumeric(pathVariableValue)) {
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
            req.setAttribute("pathVariables", getPathVariablesMap(normalizedURI));
        }

        req.setAttribute("params", getParams());

        req.setAttribute("queryParams", getQueryParams());


        chain.doFilter(req, res);
    }
}
