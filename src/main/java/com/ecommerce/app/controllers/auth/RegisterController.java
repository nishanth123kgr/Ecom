package com.ecommerce.app.controllers.auth;

import com.ecommerce.app.services.AuthService;
import com.ecommerce.app.utils.Response;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class RegisterController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        AuthService authService = new AuthService();

        Map<String, Object> responseData = authService.createUser((Map<String, Object>) req.getAttribute("params"));

        Response response = new Response(200, "User Created Successfully.", responseData);

        req.setAttribute("response", response);


    }
}
