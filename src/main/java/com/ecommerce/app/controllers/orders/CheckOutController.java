package com.ecommerce.app.controllers.orders;

import com.ecommerce.app.services.OrderService;
import com.ecommerce.app.utils.Response;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class CheckOutController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, Object> data = new OrderService().createOrder(Utils.getParams());
        Utils.setResponse( new Response((boolean) data.get("success") ? 200 : 400, data));
    }
}
