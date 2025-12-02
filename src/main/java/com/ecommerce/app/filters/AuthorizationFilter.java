package com.ecommerce.app.filters;

import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthorizationFilter extends HttpFilter {

    private static final Map<String, Map<String, String>> AUTHORIZATIONS = new HashMap<>();

    private static final String USER_SELLER_ADMIN = "user,admin,seller";
    private static final String SELLER_ADMIN = "seller,admin";
    private static final String USER_SELLER = "user,seller";

    private static final String ADMIN = "admin";
    private static final String USER = "user";
    private static final String SELLER = "seller";

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String PATCH = "PATCH";
    private static final String DELETE = "DELETE";

    static {

        Map<String, String> users = new HashMap<>();
        users.put(GET, ADMIN);

        AUTHORIZATIONS.put(".*/users$", users);

        Map<String, String> user = new HashMap<>();
        user.put(GET, USER_SELLER_ADMIN);
        user.put(PATCH, USER_SELLER_ADMIN);
        user.put(DELETE, USER_SELLER);

        AUTHORIZATIONS.put(".*/users/[0-9]+$", user);

        Map<String, String> sellers = new HashMap<>();

        sellers.put(GET, SELLER_ADMIN);

        AUTHORIZATIONS.put(".*/sellers$", sellers);

        Map<String, String> seller = new HashMap<>();

        seller.put(GET, SELLER_ADMIN);
        seller.put(PATCH, SELLER_ADMIN);
        seller.put(DELETE, ADMIN);

        AUTHORIZATIONS.put(".*/sellers/[0-9]+$", seller);

        Map<String, String> sellerRequest = new HashMap<>();

        sellerRequest.put(GET, USER_SELLER_ADMIN);
        sellerRequest.put(POST, USER_SELLER);

        AUTHORIZATIONS.put(".*/requests$", sellerRequest);

        Map<String, String> requests = new HashMap<>();

        requests.put(GET, USER_SELLER_ADMIN);
        requests.put(PATCH, ADMIN);
        requests.put(DELETE, USER_SELLER_ADMIN);

        AUTHORIZATIONS.put(".*/requests/[0-9]+$", requests);

        Map<String, String> requestApprovals = new HashMap<>();

        requestApprovals.put(GET, USER_SELLER_ADMIN);
        requestApprovals.put(POST, ADMIN);

        AUTHORIZATIONS.put(".*/requests/[0-9]+/approval$", requestApprovals);

        Map<String, String> brands = new HashMap<>();
        brands.put(GET, USER_SELLER_ADMIN);
        brands.put(POST, ADMIN);

        AUTHORIZATIONS.put(".*/brands", brands);

        Map<String, String> brand = new HashMap<>();
        brand.put(GET, USER_SELLER_ADMIN);
        brand.put(PATCH, ADMIN);
        brand.put(DELETE, ADMIN);

        AUTHORIZATIONS.put(".*/brands/[0-9]+$", brand);

        Map<String, String> categories = new HashMap<>();
        categories.put(GET, USER_SELLER_ADMIN);
        categories.put(POST, ADMIN);

        AUTHORIZATIONS.put(".*/categories", categories);

        Map<String, String> category = new HashMap<>();
        category.put(GET, USER_SELLER_ADMIN);
        category.put(PATCH, ADMIN);
        category.put(DELETE, ADMIN);

        AUTHORIZATIONS.put(".*/categories/[0-9]+$", category);


        Map<String, String> products = new HashMap<>();
        products.put(GET, USER_SELLER_ADMIN);
        products.put(POST, SELLER_ADMIN);

        AUTHORIZATIONS.put(".*/products$", products);

        Map<String, String> product = new HashMap<>();
        product.put(GET, USER_SELLER_ADMIN);
        product.put(PUT, SELLER_ADMIN);
        product.put(PATCH, SELLER_ADMIN);
        product.put(DELETE, ADMIN);

        AUTHORIZATIONS.put(".*/products/[0-9]+$", product);

        Map<String, String> productVariants = new HashMap<>();
        productVariants.put(GET, USER_SELLER_ADMIN);
        productVariants.put(POST, SELLER_ADMIN);

        AUTHORIZATIONS.put(".*/products/[0-9]+/productVariants$", productVariants);

        Map<String, String> productVariant = new HashMap<>();
        productVariant.put(GET, USER_SELLER_ADMIN);
        productVariant.put(PUT, SELLER_ADMIN);
        productVariant.put(PATCH, SELLER_ADMIN);
        productVariant.put(DELETE, ADMIN);

        AUTHORIZATIONS.put(".*/products/[0-9]+/productVariants/[0-9]+$", productVariant);

        Map<String, String> cart = new HashMap<>();
        cart.put(GET, USER);
        cart.put(POST, USER);

        AUTHORIZATIONS.put(".*/cart$", cart);

        Map<String, String> cartItem = new HashMap<>();

        cartItem.put(PUT, USER);
        cartItem.put(PATCH, USER);
        cartItem.put(DELETE, USER);

        AUTHORIZATIONS.put(".*/cart/[0-9]+$", cartItem);

        Map<String, String> checkout = new HashMap<>();
        checkout.put(GET, USER_SELLER_ADMIN);
        checkout.put(POST, USER);

        AUTHORIZATIONS.put(".*/checkout$", checkout);

        Map<String, String> addresses = new HashMap<>();
        addresses.put(GET, USER_SELLER_ADMIN);
        addresses.put(POST, USER_SELLER_ADMIN);

        AUTHORIZATIONS.put(".*/addresses$", addresses);

        Map<String, String> address = new HashMap<>();
        address.put(GET, USER_SELLER_ADMIN);
        address.put(PATCH, USER_SELLER_ADMIN);
        address.put(DELETE, USER_SELLER_ADMIN);

        AUTHORIZATIONS.put(".*/addresses/[0-9]+$", address);

        Map<String, String> orders = new HashMap<>();

        orders.put(GET, USER_SELLER_ADMIN);
        orders.put(POST, USER_SELLER_ADMIN);

        AUTHORIZATIONS.put(".*/orders$", orders);

        Map<String, String> orderItem = new HashMap<>();

        orderItem.put(PATCH, SELLER_ADMIN);

        AUTHORIZATIONS.put(".*/orders/[0-9]+$", orderItem);
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String URI = Utils.normalizeURI(req.getRequestURI(), req.getContextPath());

        if (URI.startsWith("/auth")) {
            chain.doFilter(req, res);
            return;
        }

        String method = req.getMethod();

        Map<String, Object> payload = Utils.getPayload();

        String role = (String) payload.get("role");

        for (String pattern : AUTHORIZATIONS.keySet()) {
            if (URI.matches(pattern)) {
                Map<String, String> permissions = AUTHORIZATIONS.get(pattern);

                String authorizedUsers = permissions.get(method);

                if (authorizedUsers == null) {
                    throw new APIException(ErrorCodes.METHOD_NOT_ALLOWED, method);
                }

                if (!authorizedUsers.contains(role)) {
                    throw new APIException(ErrorCodes.FORBIDDEN);
                }

                chain.doFilter(req, res);
                return;
            }
        }

        throw new APIException(ErrorCodes.CUSTOM_CLIENT_ERROR, "Invalid API Endpoint");
    }
}
