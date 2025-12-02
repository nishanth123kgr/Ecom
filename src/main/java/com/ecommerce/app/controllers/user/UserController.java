package com.ecommerce.app.controllers.user;

import com.ecommerce.app.services.UserService;
import com.ecommerce.app.utils.Response;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Map;

public class UserController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserService service = new UserService();

        Map<String, Object> pathParams = Utils.getPathParams();

        Map<String, Object> user = service.getUser(pathParams);

        Utils.setResponse(new Response(200, user));

    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse(new Response(200, new UserService().modify(Utils.mergeMaps(Utils.getPathParams(), Utils.getParams()))));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse(new Response(200, new UserService().remove(Utils.getPathParams())));

        try (Jedis jedis = Utils.getJedisPool().getResource()) {
            Utils.revokeTokens(resp, jedis, Utils.getEmail(), req.getCookies());
        }
    }
}
