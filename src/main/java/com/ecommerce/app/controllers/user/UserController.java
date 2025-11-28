package com.ecommerce.app.controllers.user;

import com.ecommerce.app.services.UserService;
import com.ecommerce.app.utils.Response;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UserController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserService service = new UserService();
        Map<String, Object> user = service.getUser((Map<String, Object>) req.getAttribute("pathVariables"));

        req.setAttribute("response", new Response(200, user));

    }
}
