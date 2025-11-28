package com.ecommerce.app.controllers.auth;

import com.ecommerce.app.services.AuthService;
import com.ecommerce.app.utils.Response;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

public class LoginController extends HttpServlet {

    public static void setToken(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> tokens, String email) {
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        Map<String, Object> params = (Map<String, Object>) req.getAttribute("params");

        Map<String, Object> tokens = new AuthService().verifyUser(params);

        setToken(req, resp, tokens, (String) params.get("email"));


    }
}
