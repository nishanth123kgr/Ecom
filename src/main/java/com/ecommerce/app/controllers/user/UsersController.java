package com.ecommerce.app.controllers.user;

import com.ecommerce.app.services.UserService;
import com.ecommerce.app.utils.Response;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class UsersController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> queryMap = Utils.getQueryParams(req);

        Map<String, Object> payload = Utils.getPayload(req);

        queryMap.put("userId", payload.get("id"));


        req.setAttribute("response", new Response(200, new UserService().getAllUsers(queryMap)));
    }
}
