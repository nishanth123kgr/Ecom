package com.ecommerce.app.controllers.requests;

import com.ecommerce.app.services.RequestService;
import com.ecommerce.app.utils.Response;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.Map;

public class RequestsController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse( new Response(200, new RequestService().getAll(Utils.getQueryParams())));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> params = Utils.getParams();

        Map<String, Object> payload = Utils.getPayload();

        int userId = (int) payload.get("id");
        params.put("userId", userId);

        Map<String, Object> requestIDMap = new RequestService().createRequest(params);

        String email = (String) payload.get("email");

        payload.put("seller_id", requestIDMap.get("seller_id"));


        JedisPool pool = Utils.getJedisPool();

        try (Jedis jedis = pool.getResource()) {
            Utils.refreshTokens(req, resp, payload, email, jedis, Utils.getTokenKey(Utils.getCookie(Utils.REFRESH_TOKEN).getValue()));
        }

        Response response = new Response(200, requestIDMap);

        Utils.setResponse( response);
    }


}
