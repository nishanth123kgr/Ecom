package com.ecommerce.app.filters;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.JWTUtils;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (Utils.normalizeURI(req.getRequestURI(), req.getContextPath()).startsWith("/auth")) {
            chain.doFilter(req, res);
            return;
        }


        Cookie[] cookies = req.getCookies();

        if (cookies == null) {
            throw new APIException(ErrorCodes.UNAUTHORIZED);
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Utils.ACCESS_TOKEN)) {
                String token = cookie.getValue();
                DecodedJWT payload = JWTUtils.verifyJWT(token);
                Map<String, Object> payloadMap = Utils.convertClaims(payload.getClaims());
                req.setAttribute("payload", payloadMap);
                Utils.setPayload((HashMap<String, Object>) payloadMap);
                chain.doFilter(req, res);
                Utils.clearThreadLocal();
                return;
            }
        }


        throw new APIException(ErrorCodes.UNAUTHORIZED);


    }
}
