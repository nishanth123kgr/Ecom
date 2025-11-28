package com.ecommerce.app.controllers.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.services.AuthService;
import com.ecommerce.app.utils.JWTUtils;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.Map;

public class RefreshController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();

        if (cookies == null) {
            throw new APIException(ErrorCodes.UNAUTHORIZED);
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                String token = cookie.getValue();
                DecodedJWT payload = JWTUtils.verifyJWT(token);
                req.setAttribute("payload", payload);

                String email = payload.getClaim("email").asString();

                String[] keys = token.split("\\.");
                String key = keys[keys.length - 1];
                JedisPool pool = (JedisPool) getServletContext().getAttribute("JEDIS_POOL");
                try (Jedis jedis = pool.getResource()) {
                    if (!jedis.sismember("rftk:" + email, "rftks:" + key)) {
                        jedis.spop("rftk:" + email);
                        for (Cookie cookieObj : cookies) {
                            cookieObj.setMaxAge(0);
                            resp.addCookie(cookieObj);
                        }
                        throw new APIException(ErrorCodes.CUSTOM_CLIENT_ERROR, "Token theft detected, all sessions closed.");
                    } else {
                        AuthService service = new AuthService();
                        Map<String, Object> tokens = service.getTokens(Utils.convertClaims(payload.getClaims()));

                        LoginController.setToken(req, resp, tokens, email);

                        jedis.srem("rftk:" + email, "rftks:" + key);

                    }
                }

                return;
            }
        }


        throw new APIException(ErrorCodes.UNAUTHORIZED);

    }
}
