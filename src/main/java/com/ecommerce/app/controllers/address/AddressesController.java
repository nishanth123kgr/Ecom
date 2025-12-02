package com.ecommerce.app.controllers.address;

import com.ecommerce.app.services.AddressService;
import com.ecommerce.app.utils.Response;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class AddressesController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse(new Response(200, new AddressService().getAll(Utils.getQueryParams())));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> data = new AddressService().addAddress(Utils.getParams());

        Utils.setResponse(new Response((boolean) data.get("success") ? 200 : 400, data));
    }
}
