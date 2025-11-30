package com.ecommerce.app.controllers.auth;

import com.ecommerce.app.services.AuthService;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public class LoginController extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        Map<String, Object> params = Utils.getParams(req);

        Map<String, Object> tokens = new AuthService().verifyUser(params);

        Utils.setTokenInRequest(req, resp, tokens, (String) params.get("email"));
    }
}
